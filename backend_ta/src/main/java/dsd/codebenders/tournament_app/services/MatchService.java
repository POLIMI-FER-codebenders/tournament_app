package dsd.codebenders.tournament_app.services;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import dsd.codebenders.tournament_app.dao.MatchRepository;
import dsd.codebenders.tournament_app.entities.*;
import dsd.codebenders.tournament_app.entities.utils.MatchStatus;
import dsd.codebenders.tournament_app.errors.CDServerUnreachableException;
import dsd.codebenders.tournament_app.errors.MatchCreationException;
import dsd.codebenders.tournament_app.requests.*;
import dsd.codebenders.tournament_app.utils.HTTPRequestsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MatchService {

    private final PlayerService playerService;
    private final CDPlayerService cdPlayerservice;
    private final CDGameClassService cdGameClassService;
    private final ServerService serverService;
    private final RoundClassChoiceService roundClassChoiceService;
    private final MatchRepository matchRepository;
    private final Logger logger = LoggerFactory.getLogger(MatchService.class);
    @Value("${tournament-app.tournament-match.mutant-validator-level:moderate}")
    private String mutantValidatorLevel;
    @Value("${tournament-app.tournament-match.max-assertions-per-test:2}")
    private int maxAssertionsPerTest;
    @Value("${tournament-app.tournament-match.auto-equivalence-threshold:0}")
    private int autoEquivalenceThreshold;
    @Value("${tournament-app.web-server.address:http://localhost:3000}")
    private String returnUrl;

    @Autowired
    public MatchService(PlayerService playerService, CDPlayerService cdPlayerservice, CDGameClassService cdGameClassService, ServerService serverService, RoundClassChoiceService roundClassChoiceService, MatchRepository matchRepository) {
        this.playerService = playerService;
        this.cdPlayerservice = cdPlayerservice;
        this.cdGameClassService = cdGameClassService;
        this.serverService = serverService;
        this.roundClassChoiceService = roundClassChoiceService;
        this.matchRepository = matchRepository;
    }

    public void addMatch(Match match) {
        matchRepository.save(match);
    }

    public void createMatchOnCD(Match match) throws MatchCreationException {
        Server server;
        try {
            server = serverService.getCDServer();
        } catch (CDServerUnreachableException e) {
            throw new MatchCreationException("Unable to create game. Caused by: " + e.getMessage());
        }
        createCDPlayers(match.getAttackersTeam(), server);
        createCDPlayers(match.getDefendersTeam(), server);
        CDGameClass cdGameClass = createCDClass(match, server);
        GameRequest gameRequest = createGameRequest(match, cdGameClass, server);
        Match createdMatch;
        try {
            createdMatch = HTTPRequestsSender.sendPostRequest(server, "/admin/api/game", gameRequest, Match.class);
        } catch (RestClientException | JsonProcessingException e) {
            throw new MatchCreationException("Unable to create game. Caused by: " + e.getMessage());
        }
        match.setStatus(MatchStatus.CREATED);
        match.setServer(server);
        match.setGameId(createdMatch.getGameId());
        matchRepository.save(match);
    }

    public void startMatchOnCD(Match match) throws MatchCreationException {
        Server server = match.getServer();
        try {
            HTTPRequestsSender.sendPostRequest(server, "/admin/api/game/start", new GameIdRequest(match), void.class);
        } catch (RestClientException | JsonProcessingException e) {
            throw new MatchCreationException("Unable to start game. Caused by: " + e.getMessage());
        }
        logger.info("Starting match " + match.getID());
        goToNextPhase(match);
    }

    public Match getOngoingMatchByPlayer(Player player) {
        Match match = matchRepository.findCreatedMatchByAttacker(player);
        if(match != null && match.getStatus() != MatchStatus.FAILED) {
            return match;
        }
        match = matchRepository.findCreatedMatchByDefender(player);
        if(match != null && match.getStatus() != MatchStatus.FAILED) {
            return match;
        }
        return null;
    }

    private void createCDPlayers(Team team, Server server) throws MatchCreationException {
        Map<String, String> queryParameters = new HashMap<>();
        List<Player> teamMembers = playerService.getPlayersByTeam(team);
        for (Player p : teamMembers) {
            CDPlayer cdPlayer = cdPlayerservice.getCDPlayerByServer(p, server);
            if (cdPlayer == null) {
                queryParameters.put("name", p.getUsername());
                try {
                    cdPlayer = HTTPRequestsSender.sendGetRequest(server, "/admin/api/auth/newUser", queryParameters, CDPlayer.class);
                } catch (RestClientException e) {
                    throw new MatchCreationException("Unable to create game. Caused by: " + e.getMessage());
                }
                cdPlayer.setServer(server);
                cdPlayer.setRealPlayer(p);
                cdPlayerservice.addNewCDPlayer(cdPlayer);
            }
        }
    }

    private CDGameClass createCDClass(Match match, Server server) throws MatchCreationException {
        GameClass gameClass = getMatchClass(match);
        CDGameClass cdGameClass = cdGameClassService.getCDGameClassByServer(gameClass, server);
        if (cdGameClass == null) {
            try {
                String source = new String(gameClass.getData(), StandardCharsets.UTF_8);
                CDClassUploadRequest body = new CDClassUploadRequest(gameClass.getFilename(), source);
                cdGameClass = HTTPRequestsSender.sendPostRequest(server, "/admin/api/class/upload", body, CDGameClass.class);
            } catch (RestClientException | JsonProcessingException e) {
                throw new MatchCreationException("Unable to create game. Caused by: " + e.getMessage());
            }
            cdGameClass.setRealClass(gameClass);
            cdGameClass.setServer(server);
            cdGameClassService.addNewCDGameClass(cdGameClass);
        }
        return cdGameClass;
    }

    private GameRequest createGameRequest(Match match, CDGameClass cdGameClass, Server server) {
        GameSettingsRequest gameSettingsRequest =
                new GameSettingsRequest("multiplayer", "easy", mutantValidatorLevel, maxAssertionsPerTest, autoEquivalenceThreshold);
        TeamRequest[] teams = new TeamRequest[2];
        teams[0] = createTeamRequest(match.getAttackersTeam(), "attacker", server);
        teams[1] = createTeamRequest(match.getDefendersTeam(), "defender", server);
        return new GameRequest(cdGameClass.getClassId(), teams, gameSettingsRequest, returnUrl);
    }

    private TeamRequest createTeamRequest(Team team, String role, Server server) {
        List<CDPlayer> cdMembers = cdPlayerservice.getCDPlayersByTeamAndServer(team, server);
        int[] members = new int[cdMembers.size()];
        for (int i = 0; i < members.length; i++) {
            members[i] = cdMembers.get(i).getUserId();
        }
        return new TeamRequest(members, role);
    }

    public List<Team> getWinnersOfRound(Tournament tournament, int roundNumber) {
        List<Match> matches = matchRepository.findByTournamentAndRoundNumber(tournament, roundNumber);
        return matches.stream().map(Match::getWinningTeam).collect(Collectors.toList());
    }

    public List<Match> getMatchesByTournamentAndRoundNumber(Tournament tournament, int round) {
        return matchRepository.findByTournamentAndRoundNumber(tournament, round);
    }

    public GameClass getMatchClass(Match match) {
        return roundClassChoiceService
                .getRoundClassChoiceByTournamentAndRound(match.getTournament(), match.getRoundNumber()).getGameClass();
    }

    public List<Match> getOngoingMatches() {
        return matchRepository.findOngoingMatches();
    }

    public Match getMatchByCDGameIdAnsServer(Integer gameId, Server server) {
        return matchRepository.findByGameIdAndServer(gameId, server);
    }

    public void setFailedMatch(Match match) {
        match.setStatus(MatchStatus.FAILED);
        matchRepository.save(match);
    }

    public Match goToNextPhase(Match match) {
        switch (match.getStatus()) {
            case CREATED -> match.setStatus(MatchStatus.IN_PHASE_ONE);
            case IN_PHASE_ONE -> match.setStatus(MatchStatus.IN_PHASE_TWO);
            case IN_PHASE_TWO -> match.setStatus(MatchStatus.IN_PHASE_THREE);
            case IN_PHASE_THREE -> match.setStatus(MatchStatus.ENDED);
        }
        return matchRepository.saveAndFlush(match);
    }

    public boolean setFailedMatchAndCheckRoundEnding(Match match) {
        synchronized (match.getTournament()) {
            setFailedMatch(match);
            long activeMatches =
                    getMatchesByTournamentAndRoundNumber(match.getTournament(), match.getRoundNumber()).stream()
                            .filter(m -> m.getStatus() != MatchStatus.ENDED && m.getStatus() != MatchStatus.FAILED).count();
            return activeMatches == 0;
        }
    }

    public boolean endMatchAndCheckRoundEnding(Match match) {
        synchronized (match.getTournament()) {
            match = goToNextPhase(match);
            logger.info("Ended match " + match.getID());
            List<Match> concurrentMatches = new ArrayList<>(getMatchesByTournamentAndRoundNumber(match.getTournament(), match.getRoundNumber()));
            logger.info("Concurrent matches:");
            for(Match m : concurrentMatches) {
                logger.info("Match " + m.getID() + " " + m.getStatus().toString());
            }
            long activeMatches = concurrentMatches.stream().filter(m -> m.getStatus() != MatchStatus.ENDED && m.getStatus() != MatchStatus.FAILED).count();
            logger.info("Number of matches still active " + activeMatches);
            return activeMatches == 0;
        }
    }

    public void setWinner(Match match, Team winner) {
        match.setWinningTeam(winner);
        matchRepository.saveAndFlush(match);
    }

    public void setLastEventSent(Match match, Long lastEventTimestamp, Long lastEventSentTime) {
        match.setLastScheduledEventTimestamp(lastEventTimestamp);
        match.setLastScheduledEventSendingTime(lastEventSentTime);
        matchRepository.save(match);
    }

    public void updateLastScoreEvent(long id, int attackersScore, int defendersScore, long timestamp) {
        matchRepository.updateLastScoreEvent(id, attackersScore, defendersScore, timestamp);
    }

    public Optional<Match> findById(Long id) {
        return matchRepository.findById(id);
    }
}
