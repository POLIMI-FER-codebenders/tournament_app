package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.*;
import dsd.codebenders.tournament_app.entities.*;
import dsd.codebenders.tournament_app.entities.utils.MatchStatus;
import dsd.codebenders.tournament_app.entities.utils.TournamentStatus;
import dsd.codebenders.tournament_app.entities.utils.TournamentType;
import dsd.codebenders.tournament_app.errors.BadRequestException;
import dsd.codebenders.tournament_app.errors.MatchCreationException;
import dsd.codebenders.tournament_app.requests.ClassChoiceRequest;
import dsd.codebenders.tournament_app.tasks.StartTournamentTask;
import dsd.codebenders.tournament_app.utils.DateUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class TournamentService {

    @Value("${tournament-app.tournament.class-selection-time-duration:60}")
    private int classSelectionTimeDuration;
    @Value("${tournament-app.tournament-match.break-time-duration:10}")
    private int breakTimeDuration;
    @Value("${tournament-app.tournament-match.phase-one-duration:10}")
    private int phaseOneDuration;
    @Value("${tournament-app.tournament-match.phase-two-duration:10}")
    private int phaseTwoDuration;
    @Value("${tournament-app.tournament-match.phase-three-duration:10}")
    private int phaseThreeDuration;
    private final Logger logger = LoggerFactory.getLogger(TournamentService.class);
    private final TournamentRepository tournamentRepository;
    private final TournamentScoreRepository tournamentScoreRepository;
    private final TeamRepository teamRepository;
    private final MatchService matchService;
    private TournamentSchedulerService tournamentSchedulerService;
    private final GameClassRepository gameClassRepository;
    private final RoundClassChoiceRepository roundClassChoiceRepository;

    @Autowired
    public TournamentService(TeamRepository teamRepository, TournamentRepository tournamentRepository, TournamentScoreRepository tournamentScoreRepository,
                             MatchService matchService, GameClassRepository gameClassRepository, RoundClassChoiceRepository roundClassChoiceRepository) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentScoreRepository = tournamentScoreRepository;
        this.teamRepository = teamRepository;
        this.matchService = matchService;
        this.gameClassRepository = gameClassRepository;
        this.roundClassChoiceRepository = roundClassChoiceRepository;
    }

    @PostConstruct
    public void init() {
        this.tournamentSchedulerService = new TournamentSchedulerService(phaseOneDuration, phaseOneDuration + phaseTwoDuration,
                phaseOneDuration + phaseTwoDuration + phaseThreeDuration, this, matchService);
        this.tournamentSchedulerService.setPoolSize(10);
        this.tournamentSchedulerService.initialize();
    }

    public Tournament findById(Long ID) {
        return tournamentRepository.findById(ID).orElse(null);
    }

    public Tournament createTournament(Tournament tournament, Player creator) {
        tournament.setCreator(creator);
        return tournamentRepository.save(tournament);
    }

    public Tournament addTeam(Tournament tournament, Team team) {
        TournamentScore tournamentScore = new TournamentScore(tournament, team);
        tournamentScoreRepository.save(tournamentScore);
        team.setInTournament(true);
        teamRepository.save(team);
        return tournamentSchedulerService.prepareRoundAndStartMatches(getTournamentByID(tournament.getID()).get());
    }

    public Tournament removeTeam(Tournament tournament, Team team) {
        tournamentScoreRepository.deleteByTeamID(team.getID());
        team.setInTournament(false);
        teamRepository.save(team);
        return getTournamentByID(tournament.getID()).get();
    }

    public Tournament tryAdvance(Tournament tournament) throws MatchCreationException {
        TournamentStatus newStatus = null;
        logger.info("Round " + tournament.getCurrentRound());
        logger.info("tryAdvance: from " + tournament.getStatus());
        switch (tournament.getStatus()) {
            case TEAMS_JOINING -> {
                if (getTournamentTeams(tournament).size() == tournament.getNumberOfTeams()) {
                    tournament.setStatus(TournamentStatus.SELECTING_CLASSES);
                    Date startDate = DateUtility.addSeconds(new Date(), classSelectionTimeDuration);
                    tournament.setStartDate(startDate);
                    tournament = tournamentRepository.save(tournament);
                    tournamentSchedulerService.schedule(new StartTournamentTask(tournament, tournamentSchedulerService), startDate);
                    return tournament;
                }
            }
            case SELECTING_CLASSES -> {
                checkRoundClassChoiceCompleteness(tournament);
                newStatus = TournamentStatus.SCHEDULING;
            }
            case IN_PROGRESS -> {
                List<Match> roundMatches = matchService.getMatchesByTournamentAndRoundNumber(tournament, tournament.getCurrentRound());
                if (roundMatches.stream().allMatch(match -> match.getStatus() == MatchStatus.ENDED)) {
                    if (tournament.getCurrentRound() >= tournament.getNumberOfRounds()) {
                        newStatus = TournamentStatus.ENDED;
                    } else {
                        newStatus = TournamentStatus.SCHEDULING;
                    }
                } else if (roundMatches.stream().anyMatch(match -> match.getStatus() == MatchStatus.FAILED)) {
                    tournament = forceTournamentEnd(tournament);
                }
            }
        }
        if (newStatus != null) {
            logger.info("tryAdvance: to " + newStatus);
            tournament.setStatus(newStatus);
            tournament = handleStatus(tournament);
        }
        return tournamentRepository.save(tournament);
    }

    private void checkRoundClassChoiceCompleteness(Tournament tournament) {
        List<GameClass> gameClassList = gameClassRepository.findAll();
        for(int round = 1; round <= tournament.getNumberOfRounds(); round++) {
            RoundClassChoice roundClassChoice = roundClassChoiceRepository.findByTournamentAndRound(tournament, round);
            if(roundClassChoice == null) {
                // no class has been chosen for that round, randomly choose one
                logger.info("randomly choosing class for round " + round);
                int randomNum = ThreadLocalRandom.current().nextInt(0, gameClassList.size());
                roundClassChoice = new RoundClassChoice(tournament, round, gameClassList.get(randomNum));
                logger.info("selected class " + roundClassChoice.getGameClass().getFilename() + " for round " + round);
                tournament.addRoundClassChoice(roundClassChoice);
                roundClassChoiceRepository.save(roundClassChoice);
                tournamentRepository.save(tournament);
            }
        }
    }

    private Tournament handleStatus(Tournament tournament) throws MatchCreationException {
        logger.info("handleStatus: " + tournament.getStatus());
        switch (tournament.getStatus()) {
            case SCHEDULING -> {
                tournament = scheduleNextRound(tournament);
                tournament.setStatus(TournamentStatus.IN_PROGRESS);
            }
            case ENDED -> {
                //TODO
                Team winningTeam;
                switch (tournament.getType()) {
                    case KNOCKOUT -> {
                        logger.info("Computing winning team for tournament " + tournament.getID() + " of type KNOCKOUT");
                        // get the winner of the last round
                        winningTeam = matchService.getWinnersOfRound(tournament, tournament.getCurrentRound()).get(0);
                    }
                    case LEAGUE -> {
                        logger.info("Computing winning team for tournament " + tournament.getID() + " of type LEAGUE");
                        // compute the team that has scored the highest
                        List<TournamentScore> tournamentScoreList = tournamentScoreRepository.findByTournament_ID(tournament.getID());
                        tournamentScoreList.sort(Comparator.comparing(TournamentScore::getLeaguePoints).reversed());
                        if(tournamentScoreList.size() > 1) {
                            if(tournamentScoreList.get(0).getLeaguePoints().equals(tournamentScoreList.get(1).getLeaguePoints())) {
                                // tie case
                                Integer highestScore = tournamentScoreList.get(0).getLeaguePoints();
                                List<TournamentScore> winningCandidates = tournamentScoreList.stream().filter(x -> x.getLeaguePoints().equals(highestScore)).collect(Collectors.toList());
                                // elect a random winner among the teams having the highest score
                                logger.info("Tournament " + tournament.getID() + " ended with a tie between two or more teams, randomly picking the winner");
                                int randomNum = ThreadLocalRandom.current().nextInt(0, winningCandidates.size());
                                winningTeam = winningCandidates.get(randomNum).getTeam();
                            } else {
                                // in the normal case, the winner is the team with the highest number of league points
                                winningTeam = tournamentScoreList.get(0).getTeam();
                            }
                        } else {
                            // there is only one team left, thus it is the automatic winner
                            winningTeam = tournamentScoreList.get(0).getTeam();
                        }
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + tournament.getType());
                }
                tournament.setWinningTeam(winningTeam);
                logger.info("The winning team of tournament " + tournament.getID() + " is " + winningTeam.getName());
                // clear inTournament variable for each team in the tournament
                List<Team> teams = tournamentScoreRepository.findByTournament_ID(tournament.getID()).stream().map(TournamentScore::getTeam).collect(Collectors.toList());
                logger.info("Number of teams: " + teams.size());
                for(Team team : teams) {
                    logger.info("Clearing variable inTournament for team " + team.getName());
                    team.setInTournament(false);
                    teamRepository.save(team);
                }
                return tournament;
            }
        }
        return tryAdvance(tournament);
    }

    private Tournament scheduleNextRound(Tournament tournament) throws MatchCreationException {
        logger.info("scheduleNextRound");
        tournament.incrementCurrentRound();
        List<Team> winners;
        if (tournament.getCurrentRound() == 1) {
            winners = getTournamentTeams(tournament);
        } else {
            winners = matchService.getWinnersOfRound(tournament, tournament.getCurrentRound()-1);
        }
        logger.info(getTournamentTeams(tournament).stream().map(Team::getID).collect(Collectors.toList()).toString());
        logger.info(winners.stream().filter(Objects::nonNull).map(Team::getID).collect(Collectors.toList()).toString());
        List<List<Long>> IDs = tournament.scheduleMatches(getTournamentTeams(tournament).stream().filter(Objects::nonNull).map(Team::getID).collect(Collectors.toList()), winners.stream().filter(Objects::nonNull).map(Team::getID).collect(Collectors.toList()));
        logger.info(IDs.toString());
        List<List<Team>> nextMatches = IDs.stream().map(ids -> ids.stream().map(id -> teamRepository.findById(id).get()).toList()).toList();
        boolean oneValid = false;
        Date roundStart = DateUtility.addSeconds(new Date(), breakTimeDuration);
        for (List<Team> teams : nextMatches) {
            int swap = (int) Math.round(Math.random()); //Randomly assign attacker and defender role
            logger.info("scheduleNextRound: " + teams.get(swap).getID() + " VS " + teams.get(1 - swap).getID());
            Match newMatch = new Match(teams.get(swap), teams.get(1 - swap), tournament.getCurrentRound(), tournament, roundStart);
            if (getScoreForTeam(tournament, teams.get(0)).get().hasForfeited()) {
                logger.info("scheduleNextRound: first team forfeited");
                newMatch.setWinningTeam(teams.get(1));
                newMatch.setStatus(MatchStatus.ENDED);
                matchService.addMatch(newMatch);
            } else if (getScoreForTeam(tournament, teams.get(1)).get().hasForfeited()) {
                logger.info("scheduleNextRound: second team forfeited");
                newMatch.setWinningTeam(teams.get(0));
                newMatch.setStatus(MatchStatus.ENDED);
                matchService.addMatch(newMatch);
            } else {
                logger.info("scheduleNextRound: valid, creating");
                matchService.addMatch(newMatch);
                matchService.createMatchOnCD(newMatch);
                logger.info("scheduleNextRound: done");
                oneValid = true;
            }
        }
        if (oneValid) {
            logger.info("scheduleNextRound: success");
        } else {
            logger.info("scheduleNextRound: none valid, advancing");
        }
        return tournament;
    }

    public Tournament forceTournamentEnd(Tournament tournament) {
        tournament.setStatus(TournamentStatus.ENDED);
        return tournamentRepository.save(tournament);
    }

    public Optional<TournamentScore> getScoreForTeam(Tournament tournament, Team team) {
        return tournamentScoreRepository.findByTeamAndTournament(team, tournament);
    }

    public List<Team> getTeams(Tournament tournament) {
        return tournament.getTournamentScores().stream().map(TournamentScore::getTeam).toList();
    }

    public List<Tournament> getTournaments() {
        return tournamentRepository.findAll();
    }

    public Optional<Tournament> getTournamentByID(Long ID) {
        return tournamentRepository.findById(ID);
    }

    public Optional<Tournament> getActiveTournamentByName(String name) {
        return tournamentRepository.findByNameIgnoreCaseAndStatusNot(name, TournamentStatus.ENDED);
    }

    public List<Tournament> getJoinedTournaments(Player player) {
        return tournamentRepository.findByTournamentScores_Team_ID(player.getTeam().getID());
    }

    public Optional<Tournament> getActiveTournament(Player player) {
        return getJoinedTournaments(player).stream().filter(t -> t.getStatus() != TournamentStatus.ENDED).findFirst();
    }

    public List<Tournament> getTournamentsOfType(TournamentType type) {
        return tournamentRepository.findAllByType(type);
    }

    public List<Team> getTournamentTeams(Tournament tournament) {
         List<TournamentScore> tournamentScoreList = tournamentScoreRepository.findByTournament_ID(tournament.getID());
         return tournamentScoreList.stream().map(TournamentScore::getTeam).collect(Collectors.toList());
    }

    public Integer getPhaseOneDuration() {
        return phaseOneDuration;
    }

    public Integer getPhaseTwoDuration() {
        return phaseTwoDuration;
    }

    public Integer getPhaseThreeDuration() {
        return phaseThreeDuration;
    }

    public RoundClassChoice postRoundChoice(ClassChoiceRequest classChoiceRequest, Player loggedPlayer) {
        Tournament tournament = tournamentRepository.findById(classChoiceRequest.getIdTournament()).orElseThrow(() -> new BadRequestException("Tournament doesn't exist."));
        if(!tournament.getCreator().equals(loggedPlayer)){
            throw new BadRequestException("Only the creator of the tournament can upload class choices.");
        }
        if(tournament.getStatus() != TournamentStatus.TEAMS_JOINING && tournament.getStatus() != TournamentStatus.SELECTING_CLASSES){
            throw new BadRequestException("Class choices can be uploaded only during TEAMS_JOINING phase.");
        }
        if(classChoiceRequest.getRoundNumber() <= 0 || tournament.getNumberOfRounds() < classChoiceRequest.getRoundNumber()){
            throw new BadRequestException("Invalid round number.");
        }
        GameClass gameClass = gameClassRepository.findById(classChoiceRequest.getClassId()).orElseThrow(() -> new BadRequestException("Class selected doesn't exist."));

        RoundClassChoice roundClassChoice = roundClassChoiceRepository.findByTournamentAndRound(tournament, classChoiceRequest.getRoundNumber());

        if(roundClassChoice != null) {
            // A class for that round has been already chosen, then update the choice
            roundClassChoice.setGameClass(gameClass);
            roundClassChoiceRepository.save(roundClassChoice);
        } else {
            // First time the class for this round is being selected, then create the entry
            roundClassChoice = new RoundClassChoice(tournament, classChoiceRequest.getRoundNumber(), gameClass);
            tournament.addRoundClassChoice(roundClassChoice);
            roundClassChoiceRepository.save(roundClassChoice);
            tournamentRepository.save(tournament);
        }
        return roundClassChoice;
    }

    public void givePointsForWin(Tournament tournament, Team winner) {
        TournamentScore score = tournamentScoreRepository.findByTeamAndTournament(winner, tournament)
                .orElseThrow(IllegalStateException::new);
        // add one point to the league points for the winner team
        score.setLeaguePoints(score.getLeaguePoints() + 1);
        tournamentScoreRepository.saveAndFlush(score);
    }
}
