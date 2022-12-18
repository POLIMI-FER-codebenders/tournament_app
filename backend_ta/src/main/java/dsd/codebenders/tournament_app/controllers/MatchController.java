package dsd.codebenders.tournament_app.controllers;

import dsd.codebenders.tournament_app.entities.*;
import dsd.codebenders.tournament_app.entities.score.MeleeScoreboard;
import dsd.codebenders.tournament_app.entities.score.MultiplayerScoreboard;
import dsd.codebenders.tournament_app.entities.score.Score;
import dsd.codebenders.tournament_app.entities.score.Scoreboard;
import dsd.codebenders.tournament_app.entities.utils.MatchType;
import dsd.codebenders.tournament_app.errors.InternalServerException;
import dsd.codebenders.tournament_app.errors.ResourceNotFoundException;
import dsd.codebenders.tournament_app.services.CDPlayerService;
import dsd.codebenders.tournament_app.services.MatchService;
import dsd.codebenders.tournament_app.services.PlayerService;
import dsd.codebenders.tournament_app.services.TournamentService;
import dsd.codebenders.tournament_app.utils.HTTPRequestsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "api/match")
public class MatchController {


    private final PlayerService playerService;
    private final CDPlayerService cdPlayerService;
    private final MatchService matchService;
    private final TournamentService tournamentService;

    @Autowired
    public MatchController(MatchService matchService, PlayerService playerService, CDPlayerService cdPlayerService, TournamentService tournamentService){
        this.playerService = playerService;
        this.matchService = matchService;
        this.cdPlayerService = cdPlayerService;
        this.tournamentService = tournamentService;
    }

    @GetMapping(value = "/current_match")
    public Map<String, String> getCurrentMatch(){
        Map<String, String> map = new HashMap<>();
        Player player = playerService.getSelf();
        Match match = matchService.getOngoingMatchByPlayer(player);
        if(match == null) {
            map.put("result", "ongoing match not found");
            return map;
        }
        Server server = match.getServer();
        CDPlayer cdPlayer = cdPlayerService.getCDPlayerByServer(player, server);
        map.put("result", "ongoing match found");
        map.put("id", match.getID().toString());
        map.put("cdId", match.getGameId().toString());
        map.put("startingDate", Long.toString(match.getStartDate().getTime()));
        map.put("server", server.getAddress());
        map.put("token", cdPlayer.getToken());
        map.put("phaseOneDuration", tournamentService.getPhaseOneDuration().toString());
        map.put("phaseTwoDuration", tournamentService.getPhaseTwoDuration().toString());
        map.put("phaseThreeDuration", tournamentService.getPhaseThreeDuration().toString());
        return map;
    }
    @GetMapping(value = "/info")
    public Object getGameStatus(@RequestParam Long gameId) throws InternalServerException {
        Match match = matchService.findById(gameId).orElseThrow(() -> new ResourceNotFoundException("Game not found"));
        Server server = match.getServer();
        GameStatus out = HTTPRequestsSender.sendGetRequest(server, "/api/game", Map.of("gameId", match.getGameId().toString()), GameStatus.class);
        Scoreboard scoreboard = out.getScoreboard();
        if (match.getTournament().getMatchType() == MatchType.MELEE) {
            for (Score score : ((MeleeScoreboard) scoreboard).getPlayers()) {
                convertScore(score, server);
            }
        } else {
            for (Score score : ((MultiplayerScoreboard) scoreboard).getAttackers()) {
                convertScore(score, server);
            }
            for (Score score : ((MultiplayerScoreboard) scoreboard).getDefenders()) {
                convertScore(score, server);
            }
        }
        return out;
    }

    private void convertScore(Score score, Server server) throws InternalServerException {
        CDPlayer cdPlayer = cdPlayerService.findByUserIdAndServer(Math.toIntExact(score.getUserId()), server)
                .orElseThrow(() -> new InternalServerException("Cannot find in database CD player with id " + score.getUserId() + " and server " + server.getAddress()));
        score.setUsername(cdPlayer.getRealPlayer().getUsername());
        score.setUserId(cdPlayer.getRealPlayer().getID());
    }
}
