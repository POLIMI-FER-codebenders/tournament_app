package dsd.codebenders.tournament_app.entities;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import dsd.codebenders.tournament_app.entities.utils.TeamRole;
import dsd.codebenders.tournament_app.responses.TeamMemberResponse;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "player")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "username")
@JsonIgnoreProperties(value = {"password"}, allowSetters = true)
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
    @JsonIgnore
    private Team team;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private TeamRole role;

    @OneToMany(mappedBy = "realPlayer", fetch = FetchType.LAZY)
    private List<CDPlayer> codeDefendersPlayers;

    public Player() {
    }


    // TODO create meaningful score (or delete if not necessary)
    public TeamMemberResponse serialize(){
        return new TeamMemberResponse(
                this.ID,
                this.username,
                this.role,
                0);
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

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Team> getTeamsCreated() {
        return teamsCreated;
    }

    public List<CDPlayer> getCodeDefendersPlayers() {
        return codeDefendersPlayers;
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
