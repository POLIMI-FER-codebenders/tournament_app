package dsd.codebenders.tournament_app.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dsd.codebenders.tournament_app.entities.utils.TeamRole;
import dsd.codebenders.tournament_app.responses.TeamMemberResponse;
import dsd.codebenders.tournament_app.serializers.TeamIDAndNameSerializer;

@Entity
@Table(name = "player")
@JsonIgnoreProperties(value = {"password"}, allowSetters = true)
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(name = "is_admin", nullable = false, columnDefinition = "Boolean default false")
    private Boolean isAdmin;

    // The teams whose this player is the creator
    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Team> teamsCreated = new ArrayList<>();

    // The list of invitations received by this player
    @OneToMany(mappedBy = "invitedPlayer", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Invitation> invitations;

    @ManyToOne
    @JoinColumn(name = "ID_team")
    @JsonSerialize(using = TeamIDAndNameSerializer.class)
    private Team team;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private TeamRole role;

    @OneToMany(mappedBy = "realPlayer", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CDPlayer> codeDefendersPlayers;

    public Player() {
    }

    public Player(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }


    // TODO create meaningful score (or delete if not necessary)
    public TeamMemberResponse serialize() {
        return new TeamMemberResponse(this.ID, this.username, this.role, 0);
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

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean isAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public TeamRole getRole() {
        return role;
    }

    public void setRole(TeamRole role) {
        this.role = role;
    }

    public void addTeamCreated(Team team) {
        this.teamsCreated.add(team);
    }

    public List<Team> getTeamsCreated() {
        return teamsCreated;
    }

    public List<CDPlayer> getCodeDefendersPlayers() {
        return codeDefendersPlayers;
    }

    public List<Invitation> getInvitations() {
        return invitations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Player player = (Player) o;
        return Objects.equals(ID, player.ID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }
}
