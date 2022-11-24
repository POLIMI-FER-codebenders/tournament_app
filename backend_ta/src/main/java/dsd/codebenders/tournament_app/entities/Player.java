package dsd.codebenders.tournament_app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "player")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private String username;
    private String email;
    @JsonIgnore
    private String password;

    // The teams whose this player is the creator
    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Team> teamsCreated;

    // The list of invitations received by this player
    @OneToMany(mappedBy = "invitedPlayer", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Invitation> invitations;

    @ManyToOne
    @JoinColumn(name = "ID_team")
    private Team team;

    public Player() {
    }

    public Long getID() {
        return ID;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Team getTeam() {
        return team;
    }

    public List<Invitation> getInvitations() {
        return invitations;
    }
}
