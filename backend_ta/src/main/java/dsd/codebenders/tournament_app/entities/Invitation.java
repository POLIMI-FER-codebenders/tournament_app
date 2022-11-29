package dsd.codebenders.tournament_app.entities;

import dsd.codebenders.tournament_app.entities.utils.InvitationStatus;

import javax.persistence.*;

@Entity
@Table(name = "invitation")
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_invited_player", nullable = false)
    private Player invitedPlayer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_team", nullable = false)
    private Team team;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private InvitationStatus status;

    public Invitation(){
    }

    public Invitation(Player invitedPlayer, Team team, InvitationStatus status) {
        this.invitedPlayer = invitedPlayer;
        this.team = team;
        this.status = status;
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

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }
}
