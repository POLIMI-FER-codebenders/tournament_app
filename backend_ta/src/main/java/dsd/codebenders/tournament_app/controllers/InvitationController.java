package dsd.codebenders.tournament_app.controllers;

import java.util.List;

import dsd.codebenders.tournament_app.entities.Invitation;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.utils.TeamRole;
import dsd.codebenders.tournament_app.errors.BadRequestException;
import dsd.codebenders.tournament_app.requests.AcceptRejectInvitationRequest;
import dsd.codebenders.tournament_app.requests.CreateInvitationRequest;
import dsd.codebenders.tournament_app.services.InvitationService;
import dsd.codebenders.tournament_app.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/invitation")
public class InvitationController {

    private final InvitationService invitationService;
    private final PlayerService playerService;

    @Autowired
    public InvitationController(InvitationService invitationService, PlayerService playerService) {
        this.invitationService = invitationService;
        this.playerService = playerService;
    }

    @GetMapping(value = "/pending")
    public List<Invitation> getPendingInvitations() {
        // Retrieve currently authenticated user from session and pass it as the creator
        Player player = playerService.getSelf();
        return invitationService.getPending(player);
    }

    @GetMapping(value = "/pending/team")
    public List<Invitation> getTeamPendingInvitations() {
        // Retrieve currently authenticated user from session and pass it as the creator
        Player player = playerService.getSelf();
        if (player.getTeam() == null) {
            throw new BadRequestException("You are not part of a team");
        }
        if (player.getRole() != TeamRole.LEADER) {
            throw new BadRequestException("You're not the leader of the team");
        }
        return invitationService.getPending(player.getTeam());
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Invitation> createInvitation(@RequestBody CreateInvitationRequest createInvitationRequest) {
        // Retrieve currently authenticated user from session and pass it as the creator
        Player player = playerService.getSelf();
        Invitation invitation = invitationService.createInvitation(player, createInvitationRequest.getIdInvitedPlayer(), createInvitationRequest.getIdTeam());
        return new ResponseEntity<>(invitation, HttpStatus.OK);
    }

    @PostMapping(value = "/accept")
    public void acceptInvitation(@RequestBody AcceptRejectInvitationRequest request) {
        // Retrieve currently authenticated user from session
        Player player = playerService.getSelf();
        invitationService.acceptInvitation(player, request.getIdInvitation());
    }

    @PostMapping(value = "/reject")
    public void rejectInvitation(@RequestBody AcceptRejectInvitationRequest request) {
        // Retrieve currently authenticated user from session
        Player player = playerService.getSelf();
        invitationService.rejectInvitation(player, request.getIdInvitation());
    }

}
