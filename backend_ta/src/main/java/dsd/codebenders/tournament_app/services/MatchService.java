package dsd.codebenders.tournament_app.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import dsd.codebenders.tournament_app.dao.MatchRepository;
import dsd.codebenders.tournament_app.entities.*;
import dsd.codebenders.tournament_app.entities.utils.MatchStatus;
import dsd.codebenders.tournament_app.errors.CDServerUnreachableException;
import dsd.codebenders.tournament_app.errors.MatchCreationException;
import dsd.codebenders.tournament_app.requests.GameRequest;
import dsd.codebenders.tournament_app.requests.GameSettingsRequest;
import dsd.codebenders.tournament_app.requests.TeamRequest;
import dsd.codebenders.tournament_app.utils.HTTPRequestsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class MatchService {

    private final CDPlayerService cdPlayerservice;
    private final ServerService serverService;
    private final MatchRepository matchRepository;
    @Value("${code-defenders.test-class-id:100}")
    private int classId;

    @Autowired
    public MatchService(CDPlayerService cdPlayerservice, ServerService serverService, MatchRepository matchRepository) {
        this.cdPlayerservice = cdPlayerservice;
        this.serverService = serverService;
        this.matchRepository = matchRepository;
    }

    public void addMatch(Match match) {
        matchRepository.save(match);
    }

    public void createAndStartMatch(Match match) throws MatchCreationException {
        Server server;
        try {
            server = serverService.getCDServer();
        } catch (CDServerUnreachableException e) {
            throw new MatchCreationException("Unable to create game. Caused by: " + e.getMessage());
        }
        createCDPlayers(match.getAttackersTeam(), server);
        createCDPlayers(match.getDefendersTeam(), server);
        GameRequest gameRequest = createGameRequest(match, server);
        Match createdMatch;
        try {
            createdMatch = HTTPRequestsSender.sendPostRequest(server, "/admin/api/game", gameRequest, Match.class);
        } catch (RestClientException | JsonProcessingException e) {
            throw new MatchCreationException("Unable to create game. Caused by: " + e.getMessage());
        }
        try {
            HTTPRequestsSender.sendPostRequest(server, "/admin/api/game/start", createdMatch, void.class);
        } catch (RestClientException | JsonProcessingException e) {
            throw new MatchCreationException("Unable to start game. Caused by: " + e.getMessage());
        }
        match.setStatus(MatchStatus.STARTED);
        match.setServer(server);
        match.setGameId(createdMatch.getGameId());
        matchRepository.save(match);
    }

    public Match getOngoingMatchByPlayer(Player player) {
        //TODO: check for FAILED
        return matchRepository.findStartedMatchByPlayer(player);
    }

    private void createCDPlayers(Team team, Server server) {
        Map<String, String> queryParameters = new HashMap<>();
        for (Player p : team.getTeamMembers()) {
            CDPlayer cdPlayer = cdPlayerservice.getCDPlayerByServer(p, server);
            if (cdPlayer == null) {
                queryParameters.put("name", p.getUsername());
                cdPlayer = HTTPRequestsSender.sendGetRequest(server, "/admin/api/auth/newUser", queryParameters, CDPlayer.class);
                cdPlayer.setServer(server);
                cdPlayer.setRealPlayer(p);
                cdPlayerservice.addNewCDPlayer(cdPlayer);
            }
        }
    }

    private GameRequest createGameRequest(Match match, Server server) {
        GameSettingsRequest gameSettingsRequest = new GameSettingsRequest("multiplayer", "easy", "moderate", 10, 10);
        TeamRequest[] teams = new TeamRequest[2];
        teams[0] = createTeamRequest(match.getAttackersTeam(), "attacker", server);
        teams[1] = createTeamRequest(match.getDefendersTeam(), "defender", server);
        return new GameRequest(classId, teams, gameSettingsRequest, "http://localhost:3000/");
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
        return matchRepository.findByTournamentAndRoundNumber(tournament, roundNumber).stream().map(Match::getWinningTeam).toList();
    }

    List<Match> getMatchesByTournamentAndRoundNumber(Tournament tournament, int round) {
        //TODO: check for FAILED
        return matchRepository.findByTournamentAndRoundNumber(tournament, round);
    }

    public void setFailedMatch(Match match) {
        match.setStatus(MatchStatus.FAILED);
        matchRepository.save(match);
    }

}
