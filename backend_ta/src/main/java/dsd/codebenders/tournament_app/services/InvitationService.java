package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.InvitationRepository;
import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.entities.Invitation;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.entities.utils.InvitationStatus;
import dsd.codebenders.tournament_app.errors.BadRequestException;
import dsd.codebenders.tournament_app.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            throw new BadRequestException("You are not the creator of this team!");
        } else {
            Invitation invitation = new Invitation(invitedPlayer, team, InvitationStatus.PENDING);
            return invitationRepository.save(invitation);
        }
    }

    public void acceptInvitation(String playerUsername, Long idInvitation) {
        Player player = playerRepository.findByUsername(playerUsername);
        Invitation invitation = invitationRepository.findById(idInvitation).orElseThrow(() -> new ResourceNotFoundException("Invitation doesn't exist!"));
        if(!invitation.getInvitedPlayer().equals(player)){
            throw new BadRequestException("You can't accept others' invitations.");
        }
        if(invitation.getStatus() != InvitationStatus.PENDING){
            throw new BadRequestException("You can't accept this invitation.");
        }
        Team team = invitation.getTeam();
        if(team.isFull()){
            invitationRepository.delete(invitation);
            throw new BadRequestException("The team is full, you can't no longer join.");
        }
        if(team.isInTournament()){
            throw new BadRequestException("The team is currently involved in a tournament.");
        }
        if(team.getTeamMembers().contains(player)){
            invitationRepository.delete(invitation);
            throw new BadRequestException("You are already part of the team!");
        }
        team.addMember(player);
        teamRepository.save(team);
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitationRepository.save(invitation);
    }

    public void rejectInvitation(String playerUsername, Long idInvitation) {
        Player player = playerRepository.findByUsername(playerUsername);
        Invitation invitation = invitationRepository.findById(idInvitation).orElseThrow(() -> new ResourceNotFoundException("Invitation doesn't exist!"));
        if(!invitation.getInvitedPlayer().equals(player)){
            throw new BadRequestException("You can't reject others' invitations.");
        }
        if(invitation.getStatus() != InvitationStatus.PENDING){
            throw new BadRequestException("You can't reject this invitation.");
        }
        invitation.setStatus(InvitationStatus.REJECTED);
        invitationRepository.save(invitation);
    }
}
