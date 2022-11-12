package dsd.codebenders.tournament_app.dao;

import dsd.codebenders.tournament_app.entities.Invitation;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.utils.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    public List<Invitation> findByInvitedPlayerAndStatusEquals(Player invitedPlayer, InvitationStatus invitationStatus);
}
