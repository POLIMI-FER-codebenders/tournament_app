package dsd.codebenders.tournament_app.controllers;

import dsd.codebenders.tournament_app.entities.CDPlayer;
import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.services.CDPlayerService;
import dsd.codebenders.tournament_app.services.MatchService;
import dsd.codebenders.tournament_app.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "api/match")
public class MatchController {
    private final PlayerService playerService;
    private final CDPlayerService cdPlayerService;
    private final MatchService matchService;

    @Autowired
    public MatchController(MatchService matchService, PlayerService playerService, CDPlayerService cdPlayerService){
        this.playerService = playerService;
        this.matchService = matchService;
        this.cdPlayerService = cdPlayerService;
    }

    @GetMapping(value = "/current_match")
    public Map<String, String> createTeam(){
        Map<String, String> map = new HashMap<>();
        Player player = playerService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        Match match = matchService.getOngoingMatchByPlayer(player);
        if(match == null) {
            map.put("result", "ongoing match not found");
            return map;
        }
        String server = match.getServer();
        CDPlayer cdPlayer = cdPlayerService.getCDPlayerByServer(player, server);
        map.put("result", "ongoing match found");
        map.put("server", server);
        map.put("token", cdPlayer.getToken());
        return map;
    }
}
