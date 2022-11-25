package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.MatchRepository;
import dsd.codebenders.tournament_app.entities.CDPlayer;
import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.errors.HTTPResponseException;
import dsd.codebenders.tournament_app.errors.MatchCreationException;
import dsd.codebenders.tournament_app.requests.GameRequest;
import dsd.codebenders.tournament_app.requests.GameSettingsRequest;
import dsd.codebenders.tournament_app.requests.TeamRequest;
import dsd.codebenders.tournament_app.utils.HTTPRequestsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MatchService {

    private final TeamService teamService;
    private final CDPlayerService cdPlayerservice;
    private final MatchRepository matchRepository;

    @Autowired
    public MatchService(TeamService teamService, CDPlayerService cdPlayerservice, MatchRepository matchRepository) {
        this.teamService = teamService;
        this.cdPlayerservice = cdPlayerservice;
        this.matchRepository = matchRepository;
    }

    public void createAndStartMatch(Match match) throws MatchCreationException {
        // TODO: choose CD server
        String server = "http://localhost:4000";
        createCDPlayers(match.getAttackersTeam(), server);
        createCDPlayers(match.getDefendersTeam(), server);
        GameRequest gameRequest = createGameRequest(match);
        Match createdMatch;
        try {
            createdMatch = HTTPRequestsSender.sendPostRequest(server + "/admin/api/game", gameRequest, Match.class);
        } catch (HTTPResponseException e) {
            throw new MatchCreationException("Unable to create game");
        }
        try {
            HTTPRequestsSender.sendPostRequest(server + "/admin/api/game/start", createdMatch, void.class);
        } catch (HTTPResponseException e) {
            throw new MatchCreationException("Unable to start game");
        }
        matchRepository.setCreatedMatchById(match.getID(), server, createdMatch.getGameId());
    }

    public Match getOngoingMatchByPlayer(Player player) {
        // TODO: implement findByPlayer
        //return matchRepository.findByPlayer(Player player);
        return new Match();
    }

    private void createCDPlayers(Team team, String server) {
        Map<String, String> queryParameters = new HashMap<>();
        for(Player p: team.getMembers()) {
            CDPlayer cdPlayer = cdPlayerservice.getCDPlayerByServer(p, server);
            if(cdPlayer == null) {
                queryParameters.put("username", p.getUsername());
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
        teams[0] = teamService.createTeamRequest(match.getAttackersTeam(), "attacker");
        teams[1] = teamService.createTeamRequest(match.getDefendersTeam(), "defender");
        return new GameRequest(1, teams, gameSettingsRequest, "http://localhost:3000/");
    }

}
