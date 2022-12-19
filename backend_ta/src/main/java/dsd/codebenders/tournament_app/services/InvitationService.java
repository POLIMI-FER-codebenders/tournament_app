package dsd.codebenders.tournament_app.services;

import java.util.List;

import dsd.codebenders.tournament_app.dao.InvitationRepository;
import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.entities.Invitation;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.entities.utils.InvitationStatus;
import dsd.codebenders.tournament_app.entities.utils.TeamRole;
import dsd.codebenders.tournament_app.errors.BadRequestException;
import dsd.codebenders.tournament_app.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<Invitation> getPending(Player invitedPlayer) {
        return invitationRepository.findByInvitedPlayerAndStatusEquals(invitedPlayer, InvitationStatus.PENDING);
    }

    public List<Invitation> getPending(Team team) {
        return invitationRepository.findByTeamAndStatus(team, InvitationStatus.PENDING);
    }

    public Invitation createInvitation(Player sender, Long IDInvitedPlayer, Long IDTeam) {
        Player invitedPlayer = playerRepository.findById(IDInvitedPlayer).orElseThrow(() -> new ResourceNotFoundException("Invalid invited player"));
        Team team = teamRepository.findById(IDTeam).orElseThrow(() -> new ResourceNotFoundException("Invalid team"));
        if (!team.getCreator().equals(sender)) {
            throw new BadRequestException("You are not the creator of this team!");
        } else if(invitationRepository.existsByInvitedPlayerAndTeamAndStatus(invitedPlayer, team, InvitationStatus.PENDING)) {
            throw new BadRequestException("You have already invited this player to join this team!");
        } else if(team.equals(invitedPlayer.getTeam())) {
            throw new BadRequestException("The player is already in the team.");
        } else {
            Invitation invitation = new Invitation(invitedPlayer, team, InvitationStatus.PENDING);
            return invitationRepository.save(invitation);
        }
    }

    public void acceptInvitation(Player player, Long idInvitation) {
        Invitation invitation = invitationRepository.findById(idInvitation).orElseThrow(() -> new ResourceNotFoundException("Invitation doesn't exist!"));
        if (!invitation.getInvitedPlayer().equals(player)) {
            throw new BadRequestException("You can't accept others' invitations.");
        }

        if(player.getTeam() != null){
            throw new BadRequestException("You are already in a team! Leave your current team before joining another one.");
        }
        if(invitation.getStatus() != InvitationStatus.PENDING){
            throw new BadRequestException("You can't accept this invitation.");
        }
        Team team = invitation.getTeam();
        if (team.isFull()) {
            invitationRepository.delete(invitation);
            throw new BadRequestException("The team is full, you can no longer join.");
        }
        if (team.isInTournament()) {
            throw new BadRequestException("The team is currently involved in a tournament.");
        }
        if (team.getTeamMembers().contains(player)) {
            invitationRepository.delete(invitation);
            throw new BadRequestException("You are already part of the team!");
        }
        player.setTeam(team);
        player.setRole(TeamRole.MEMBER);
        playerRepository.save(player);
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitationRepository.save(invitation);
    }

    public void rejectInvitation(Player player, Long idInvitation) {
        Invitation invitation = invitationRepository.findById(idInvitation).orElseThrow(() -> new ResourceNotFoundException("Invitation doesn't exist!"));
        if (!invitation.getInvitedPlayer().equals(player)) {
            throw new BadRequestException("You can't reject others' invitations.");
        }
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new BadRequestException("You can't reject this invitation.");
        }
        invitation.setStatus(InvitationStatus.REJECTED);
        invitationRepository.save(invitation);
    }

    public void deleteAllForTeam(Team team) {
        invitationRepository.deleteByTeamAndStatus(team,InvitationStatus.PENDING); //Keep accepted and rejected for history
    }
}
