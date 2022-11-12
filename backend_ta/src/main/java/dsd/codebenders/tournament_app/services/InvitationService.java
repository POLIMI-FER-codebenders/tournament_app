package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.InvitationRepository;
import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.entities.Invitation;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.entities.utils.InvitationStatus;
import dsd.codebenders.tournament_app.errors.RequestNotAuthorizedException;
import dsd.codebenders.tournament_app.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class InvitationService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final InvitationRepository invitationRepository;

    @Autowired
    public InvitationService(PlayerRepository playerRepository, TeamRepository teamRepository, InvitationRepository invitationRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
        this.invitationRepository = invitationRepository;
    }

    public List<Invitation> getPending(String invitedPlayerUsername){
        Player invitedPlayer = playerRepository.findByUsername(invitedPlayerUsername);
         return invitationRepository.findByInvitedPlayerAndStatusEquals(invitedPlayer, InvitationStatus.PENDING);
    }

    public Invitation createInvitation(String senderUsername, Long IDInvitedPlayer, Long IDTeam){
        Player sender = playerRepository.findByUsername(senderUsername);
        Player invitedPlayer = playerRepository.findById(IDInvitedPlayer).orElseThrow(() -> new ResourceNotFoundException("Invalid invited player"));
        Team team = teamRepository.findById(IDTeam).orElseThrow(() -> new ResourceNotFoundException("Invalid team"));
        if(!team.getCreator().equals(sender)){
            throw new RequestNotAuthorizedException("You are not the creator of this team!");
        } else {
            Invitation invitation = new Invitation(invitedPlayer, team, InvitationStatus.PENDING);
            return invitationRepository.save(invitation);
        }
    }

}
