package dsd.codebenders.tournament_app.entities;

import dsd.codebenders.tournament_app.entities.utils.InvitationStatus;

import javax.persistence.*;

@Entity
@Table(name = "invitation")
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @ManyToOne
    @JoinColumn(name = "ID_invited_player")
    private Player invitedPlayer;

    @ManyToOne
    @JoinColumn(name = "ID_team")
    private Team team;

    @Column(name = "status")
    private InvitationStatus status;

    public Invitation(){
    }

    public Long getID() {
        return ID;
    }

    public Player getInvitedPlayer() {
        return invitedPlayer;
    }

    public Team getTeam() {
        return team;
    }
}
