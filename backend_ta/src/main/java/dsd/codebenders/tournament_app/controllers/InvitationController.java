package dsd.codebenders.tournament_app.controllers;

import dsd.codebenders.tournament_app.entities.Invitation;
import dsd.codebenders.tournament_app.errors.RequestNotAuthorizedException;
import dsd.codebenders.tournament_app.errors.ResourceNotFoundException;
import dsd.codebenders.tournament_app.requests.CreateInvitationRequest;
import dsd.codebenders.tournament_app.services.InvitationService;
import dsd.codebenders.tournament_app.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/invitation")
public class InvitationController {

    private PlayerService playerService;
    private InvitationService invitationService;

    @Autowired
    public InvitationController(PlayerService playerService, InvitationService invitationService) {
        this.playerService = playerService;
        this.invitationService = invitationService;
    }

    @GetMapping(value = "pending")
    public List<Invitation> getPendingInvitations(){
        // Retrieve currently authenticated user from session and pass it as the creator
        return invitationService.getPending("andrea");
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Invitation> createInvitation(@RequestBody CreateInvitationRequest createInvitationRequest){
        // Retrieve currently authenticated user from session and pass it as the creator
        try {
            Invitation invitation = invitationService.createInvitation("ciccio", createInvitationRequest.getIdInvitedPlayer(), createInvitationRequest.getIdTeam());
            return new ResponseEntity<>(invitation, HttpStatus.OK);
        } catch (RequestNotAuthorizedException e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping(value = "accept")
    public void acceptInvitation(){

    }

}
