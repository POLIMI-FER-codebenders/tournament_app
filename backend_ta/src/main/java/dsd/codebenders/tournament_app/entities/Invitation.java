package dsd.codebenders.tournament_app.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonManagedReference
    private Player invitedPlayer;

    @ManyToOne
    @JoinColumn(name = "ID_team")
    @JsonManagedReference
    private Team team;

    @Column(name = "status")
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
}
