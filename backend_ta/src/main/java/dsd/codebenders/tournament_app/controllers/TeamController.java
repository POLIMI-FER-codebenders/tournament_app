package dsd.codebenders.tournament_app.controllers;

import java.util.List;

import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.errors.BadRequestException;
import dsd.codebenders.tournament_app.requests.GetTeamRequest;
import dsd.codebenders.tournament_app.requests.JoinTeamRequest;
import dsd.codebenders.tournament_app.requests.KickMemberFromTeamRequest;
import dsd.codebenders.tournament_app.requests.PromoteToTeamLeaderRequest;
import dsd.codebenders.tournament_app.responses.TeamMemberResponse;
import dsd.codebenders.tournament_app.responses.TeamResponse;
import dsd.codebenders.tournament_app.services.PlayerService;
import dsd.codebenders.tournament_app.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
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
    public TeamController(TeamService teamService, PlayerService playerService) {
        this.playerService = playerService;
        this.teamService = teamService;
    }

    @GetMapping(value = "/get-mine")
    public Team getMyTeam() {
        return playerService.getSelf().getTeam();
    }

    @GetMapping(value = "/get")
    public Team getTeam(@RequestParam(name = "id") Long idTeam){
        if(idTeam == null){
            throw new BadRequestException("Invalid arguments");
        }
        return teamService.findById(idTeam);
    }

    @GetMapping(value = "/get-all")
    public List<TeamResponse> getAllTeams() {
        return teamService.findAll();
    }

    @GetMapping(value = "/members/get-all")
    public List<TeamMemberResponse> getAllMembers(@RequestParam(name = "id") Long idTeam){
        if(idTeam == null){
            throw new BadRequestException("Invalid arguments");
        }
        return teamService.getAllMembers(idTeam);
    }


    @PostMapping(value = "/create")
    public Team createTeam(@RequestBody Team team) {
        Player creator = playerService.getSelf();
        return teamService.createTeam(team, creator);
    }

    @PostMapping(value = "/join")
    public void joinTeam(@RequestBody JoinTeamRequest request) {
        // retrieve currently authenticated user from session
        Player player = playerService.getSelf();
        Team team = teamService.findById(request.getIdTeam());
        teamService.joinTeam(player, team);
    }

    @PostMapping(value = "/leave")
    public void leaveTeam() {
        // retrieve currently authenticated user from session
        Player player = playerService.getSelf();
        teamService.leaveTeam(player);
    }

    @PostMapping(value = "/kick-member")
    public void kickMember(@RequestBody KickMemberFromTeamRequest kickMemberFromTeamRequest) {
        if (kickMemberFromTeamRequest == null || kickMemberFromTeamRequest.getIdKickedPlayer() == null) {
            throw new BadRequestException("Invalid arguments");
        }
        // retrieve currently authenticated user from session
        String loggedPlayerUsername = playerService.getSelf().getUsername();
        Long playerToKickId = kickMemberFromTeamRequest.getIdKickedPlayer();
        teamService.kickMember(loggedPlayerUsername, playerToKickId);
    }

    @PostMapping(value = "/members/promote-leader")
    public void promoteToLeader(@RequestBody PromoteToTeamLeaderRequest promoteToTeamLeaderRequest) {
        Player playerLogged = playerService.getSelf();
        Player playerToPromote = playerService.findById(promoteToTeamLeaderRequest.getIdPlayer());

        teamService.promoteToLeader(playerLogged, playerToPromote);

    }

}