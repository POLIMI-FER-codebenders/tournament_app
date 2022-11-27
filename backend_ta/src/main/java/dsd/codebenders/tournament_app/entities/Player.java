package dsd.codebenders.tournament_app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import dsd.codebenders.tournament_app.entities.utils.TeamRole;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "player")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "username")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
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
    @JsonIgnore
    private Team team;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private TeamRole role;

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

    public TeamRole getRole() {
        return role;
    }

    public void setRole(TeamRole role) {
        this.role = role;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public List<Invitation> getInvitations() {
        return invitations;
    }
}
