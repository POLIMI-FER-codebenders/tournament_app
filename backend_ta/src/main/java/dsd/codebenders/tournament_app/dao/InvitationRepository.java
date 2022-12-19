package dsd.codebenders.tournament_app.dao;

import java.util.List;

import dsd.codebenders.tournament_app.entities.Invitation;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.entities.utils.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    @Transactional
    @Modifying
    int deleteByTeamAndStatus(Team team, InvitationStatus status);

    public List<Invitation> findByInvitedPlayerAndStatusEquals(Player invitedPlayer, InvitationStatus invitationStatus);

    public List<Invitation> findByTeamAndStatus(Team team, InvitationStatus status);

    public boolean existsByInvitedPlayerAndTeamAndStatus(Player invitedPlayer, Team team, InvitationStatus status);
}
