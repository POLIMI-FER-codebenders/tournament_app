package dsd.codebenders.tournament_app.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import dsd.codebenders.tournament_app.dao.MatchRepository;
import dsd.codebenders.tournament_app.entities.CDPlayer;
import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.errors.MatchCreationException;
import dsd.codebenders.tournament_app.requests.GameRequest;
import dsd.codebenders.tournament_app.requests.GameSettingsRequest;
import dsd.codebenders.tournament_app.requests.TeamRequest;
import dsd.codebenders.tournament_app.utils.HTTPRequestsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MatchService {

    private final CDPlayerService cdPlayerservice;
    private final MatchRepository matchRepository;
    @Value("${code-defenders.address:http://localhost:4000}")
    private String server;
    @Value("${code-defenders.test-class-id:100}")
    private int classId;

    @Autowired
    public MatchService(CDPlayerService cdPlayerservice, MatchRepository matchRepository) {
        this.cdPlayerservice = cdPlayerservice;
        this.matchRepository = matchRepository;
    }

    public void createAndStartMatch(Match match) throws MatchCreationException {
        // TODO: choose CD server
        createCDPlayers(match.getAttackersTeam());
        createCDPlayers(match.getDefendersTeam());
        GameRequest gameRequest = createGameRequest(match);
        Match createdMatch;
        try {
            createdMatch = HTTPRequestsSender.sendPostRequest(server + "/admin/api/game", gameRequest, Match.class);
        } catch (RestClientException | JsonProcessingException e) {
            throw new MatchCreationException("Unable to create game. Caused by: " + e.getMessage());
        }
        try {
            HTTPRequestsSender.sendPostRequest(server + "/admin/api/game/start", createdMatch, void.class);
        } catch (RestClientException | JsonProcessingException e) {
            throw new MatchCreationException("Unable to start game. Caused by: " + e.getMessage());
        }
        matchRepository.setCreatedMatchById(match.getID(), server, createdMatch.getGameId());
    }

    public Match getOngoingMatchByPlayer(Player player) {
        return matchRepository.findStartedMatchByPlayer(player);
    }

    private void createCDPlayers(Team team) {
        Map<String, String> queryParameters = new HashMap<>();
        for(Player p: team.getMembers()) {
            CDPlayer cdPlayer = cdPlayerservice.getCDPlayerByServer(p, server);
            if(cdPlayer == null) {
                queryParameters.put("name", p.getUsername());
                cdPlayer = HTTPRequestsSender.sendGetRequest(server + "/admin/api/auth/newUser", queryParameters, CDPlayer.class);
                cdPlayer.setServer(server);
                cdPlayer.setRealPlayer(p);
                cdPlayerservice.addNewCDPlayer(cdPlayer);
            }
        }
    }

    private GameRequest createGameRequest(Match match) {
        GameSettingsRequest gameSettingsRequest = new GameSettingsRequest("multiplayer", "easy", "moderate", 10, 10);
        TeamRequest [] teams = new TeamRequest[2];
        teams[0] = createTeamRequest(match.getAttackersTeam(), "attacker");
        teams[1] = createTeamRequest(match.getDefendersTeam(), "defender");
        return new GameRequest(classId, teams, gameSettingsRequest, "http://localhost:3000/");
    }

    private TeamRequest createTeamRequest(Team team, String role) {
        List<CDPlayer> cdMembers = cdPlayerservice.getCDPlayersByTeamAndServer(team, server);
        int [] members = new int[cdMembers.size()];
        for(int i = 0; i < members.length; i++) {
            members[i] = cdMembers.get(i).getUserId();
        }
        return new TeamRequest(members, role);
    }

}
