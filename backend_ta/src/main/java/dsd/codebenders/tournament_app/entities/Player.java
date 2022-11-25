package dsd.codebenders.tournament_app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "player")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private String username;
    private String email;
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

    @OneToMany(mappedBy = "realPlayer", fetch = FetchType.LAZY)
    private List<CDPlayer> codeDefendersPlayers;

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

    public List<Team> getTeamsCreated() {
        return teamsCreated;
    }

    public Team getTeam() {
        return team;
    }

    public List<CDPlayer> getCodeDefendersPlayers() {
        return codeDefendersPlayers;
    }

    public List<Invitation> getInvitations() {
        return invitations;
    }
}
