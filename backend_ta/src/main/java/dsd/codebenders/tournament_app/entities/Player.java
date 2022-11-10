package dsd.codebenders.tournament_app.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "player")
public class Player {
    @Id
    private Long ID;
    private String username;

    // The teams whose this player is the creator
    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
    private List<Team> teamsCreated;

    // The list of invitations received by this player
    @OneToMany(mappedBy = "invitedPlayer", fetch = FetchType.LAZY)
    private List<Invitation> invitations;

    public Player() {
    }

    public Long getID() {
        return ID;
    }

    public String getUsername() {
        return username;
    }

    public List<Team> getTeamsCreated() {
        return teamsCreated;
    }

    public List<Invitation> getInvitations() {
        return invitations;
    }
}
