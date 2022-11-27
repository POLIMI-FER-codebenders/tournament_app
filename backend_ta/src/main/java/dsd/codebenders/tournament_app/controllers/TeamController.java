package dsd.codebenders.tournament_app.controllers;

import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.errors.BadRequestException;
import dsd.codebenders.tournament_app.requests.GetTeamRequest;
import dsd.codebenders.tournament_app.requests.JoinTeamRequest;
import dsd.codebenders.tournament_app.requests.KickMemberFromTeamRequest;
import dsd.codebenders.tournament_app.responses.TeamMemberResponse;
import dsd.codebenders.tournament_app.services.PlayerService;
import dsd.codebenders.tournament_app.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping(value = "/get-mine")
    public Team getMyTeam(){
        return playerService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).getTeam();
    }

    @GetMapping(value = "/get")
    public Team getTeam(@RequestBody GetTeamRequest getTeamRequest){
        if(getTeamRequest.getIdTeam() == null){
            throw new BadRequestException("Invalid arguments");
        }
        return teamService.findById(getTeamRequest.getIdTeam());
    }

    @GetMapping(value = "/members/get-all")
    public List<TeamMemberResponse> getAllMembers(@RequestBody GetTeamRequest getTeamRequest){
        if(getTeamRequest.getIdTeam() == null){
            throw new BadRequestException("Invalid arguments");
        }
        return teamService.getAllMembers(getTeamRequest.getIdTeam());
    }

    @PostMapping(value = "/create")
    public Team createTeam(@RequestBody Team team){
        // retrieve currently authenticated user from session
        Player creator = playerService.findByUsername("ciccio");
        return teamService.createTeam(team, creator);
    }

    @PostMapping(value = "/join")
    public void joinTeam(@RequestBody JoinTeamRequest request){
        // retrieve currently authenticated user from session
        Player player = playerService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        Team team = teamService.findById(request.getIdTeam());
        teamService.joinTeam(player, team);
    }

    @PostMapping(value = "/leave")
    public void leaveTeam(){
        // retrieve currently authenticated user from session
        Player player = playerService.findByUsername("ciccio");
        teamService.leaveTeam(player);
    }

    @PostMapping(value = "/kick_member")
    public void kickMember(@RequestBody KickMemberFromTeamRequest kickMemberFromTeamRequest){
        if(kickMemberFromTeamRequest == null || kickMemberFromTeamRequest.getIdKickedPlayer() == null){
            throw new BadRequestException("Invalid arguments");
        }
        // retrieve currently authenticated user from session
        String loggedPlayerUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Long playerToKickId = kickMemberFromTeamRequest.getIdKickedPlayer();
        teamService.kickMember(loggedPlayerUsername, playerToKickId);
    }

}
