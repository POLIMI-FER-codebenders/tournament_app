package dsd.codebenders.tournament_app.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.dao.TournamentRepository;
import dsd.codebenders.tournament_app.dao.TournamentScoreRepository;
import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.entities.Tournament;
import dsd.codebenders.tournament_app.entities.TournamentScore;
import dsd.codebenders.tournament_app.entities.utils.MatchStatus;
import dsd.codebenders.tournament_app.entities.utils.TournamentStatus;
import dsd.codebenders.tournament_app.entities.utils.TournamentType;
import dsd.codebenders.tournament_app.errors.MatchCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TournamentService {

    private final Logger logger = LoggerFactory.getLogger(TournamentService.class);
    private final TournamentRepository tournamentRepository;
    private final TournamentScoreRepository tournamentScoreRepository;
    private final TeamRepository teamRepository;
    private final MatchService matchService;

    @Autowired
    public TournamentService(TeamRepository teamRepository, TournamentRepository tournamentRepository, TournamentScoreRepository tournamentScoreRepository,
                             MatchService matchService) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentScoreRepository = tournamentScoreRepository;
        this.teamRepository = teamRepository;
        this.matchService = matchService;
    }

    public Tournament findById(Long ID) {
        return tournamentRepository.findById(ID).orElse(null);
    }

    public Tournament createTournament(Tournament tournament, Player creator) {
        tournament.setCreator(creator);
        return tournamentRepository.save(tournament);
    }

    public Tournament addTeam(Tournament tournament, Team team) throws MatchCreationException {
        TournamentScore tournamentScore = new TournamentScore(tournament, team);
        tournamentScoreRepository.save(tournamentScore);
        team.setInTournament(true);
        teamRepository.save(team);
        return tryAdvance(getTournamentByID(tournament.getID()).get());
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
                    newStatus = TournamentStatus.SCHEDULING;
                }
            }
            case IN_PROGRESS -> {
                if (matchService.getMatchesByTournamentAndRoundNumber(tournament, tournament.getCurrentRound()).stream()
                        .allMatch(match -> match.getStatus() == MatchStatus.ENDED)) {
                    if (tournament.getCurrentRound() >= tournament.getNumberOfRounds()) {
                        newStatus = TournamentStatus.ENDED;
                    } else {
                        newStatus = TournamentStatus.SCHEDULING;
                    }
                }
            }
        }
        logger.info("tryAdvance: to " + newStatus);
        if (newStatus != null) {
            tournament.setStatus(newStatus);
            tournament = handleStatus(tournament);
        }
        return tournamentRepository.save(tournament);
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
            winners = matchService.getWinnersOfRound(tournament, tournament.getCurrentRound());
        }
        logger.error(getTournamentTeams(tournament).stream().map(Team::getID).toList().toString());
        logger.error(winners.stream().map(Team::getID).toList().toString());
        List<List<Long>> IDs = tournament.scheduleMatches(getTournamentTeams(tournament).stream().map(Team::getID).toList(), winners.stream().map(Team::getID).toList());
        logger.error(IDs.toString());
        List<List<Team>> nextMatches = IDs.stream().map(ids -> ids.stream().map(id -> teamRepository.findById(id).get()).toList()).toList();
        Boolean oneValid = false;
        for (List<Team> teams : nextMatches) {
            int swap = (int) Math.round(Math.random()); //Randomly assign attacker and defender role
            logger.info("scheduleNextRound: " + teams.get(swap).getID() + " VS " + teams.get(1 - swap).getID());
            Match newMatch = new Match(teams.get(swap), teams.get(1 - swap), tournament.getCurrentRound(), tournament);
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
                matchService.createAndStartMatch(newMatch);
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

    public Optional<TournamentScore> getScoreForTeam(Tournament tournament, Team team) {
        return tournament.getTournamentScores().stream().filter(ts -> ts.getTeam().equals(team)).findFirst();
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
        return tournamentScoreRepository.findByTournament_ID(tournament.getID()).stream().map(TournamentScore::getTeam).toList();
    }

    public List<Match> getMatchesInCurrentRound(Tournament tournament) {
        return tournament.getMatches().stream().filter(m -> Objects.equals(m.getRoundNumber(), tournament.getCurrentRound())).toList();
    }

}
