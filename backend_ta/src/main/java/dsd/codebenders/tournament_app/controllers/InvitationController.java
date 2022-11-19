package dsd.codebenders.tournament_app.controllers;

import dsd.codebenders.tournament_app.entities.Invitation;
import dsd.codebenders.tournament_app.requests.AcceptRejectInvitationRequest;
import dsd.codebenders.tournament_app.requests.CreateInvitationRequest;
import dsd.codebenders.tournament_app.services.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/invitation")
public class InvitationController {

    private final InvitationService invitationService;

    @Autowired
    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @GetMapping(value = "/pending")
    public List<Invitation> getPendingInvitations(){
        // Retrieve currently authenticated user from session and pass it as the creator
        return invitationService.getPending("andrea");
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Invitation> createInvitation(@RequestBody CreateInvitationRequest createInvitationRequest){
        // Retrieve currently authenticated user from session and pass it as the creator
        Invitation invitation = invitationService.createInvitation("ciccio", createInvitationRequest.getIdInvitedPlayer(), createInvitationRequest.getIdTeam());
        return new ResponseEntity<>(invitation, HttpStatus.OK);
    }

    @PostMapping(value = "/accept")
    public void acceptInvitation(@RequestBody AcceptRejectInvitationRequest request){
        // Retrieve currently authenticated user from session
        invitationService.acceptInvitation("andrea", request.getIdInvitation());
    }

    @PostMapping(value = "/reject")
    public void rejectInvitation(@RequestBody AcceptRejectInvitationRequest request){
        // Retrieve currently authenticated user from session
        invitationService.rejectInvitation("giuseppe", request.getIdInvitation());
    }

}
