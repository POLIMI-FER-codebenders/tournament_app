package dsd.codebenders.tournament_app.controllers;

import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.services.PlayerService;
import dsd.codebenders.tournament_app.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/team")
public class TeamController {

    private final PlayerService playerService;
    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService, PlayerService playerService){
        this.playerService = playerService;
        this.teamService = teamService;
    }

    @PostMapping(value = "/create")
    public Team createTeam(@RequestBody Team team){
        // Retrieve currently authenticated user from session
        Player creator = playerService.findByUsername("ciccio");
        return teamService.createTeam(team, creator);
    }

    @PostMapping(value = "/kick_member")
    public void kickMember(){

    }

}
