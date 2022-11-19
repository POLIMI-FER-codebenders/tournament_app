package dsd.codebenders.tournament_app.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dsd.codebenders.tournament_app.entities.utils.TeamPolicy;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "team")
@JsonIgnoreProperties({ "creator" })
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    private String name;

    @Column(name = "max_number_of_players")
    private int maxNumberOfPlayers;

    @ManyToOne
    @JoinColumn(name = "ID_creator")
    private Player creator;

    @ManyToMany
    @JoinTable(
            name = "player_team_link",
            joinColumns = @JoinColumn(name = "ID_team"),
            inverseJoinColumns = @JoinColumn(name = "ID_player")
    )
    Set<Player> teamMembers;

    @Column(name = "policy")
    @Enumerated(EnumType.STRING)
    private TeamPolicy policy;

    @Column(name = "in_tournament")
    private boolean isInTournament;

    public Long getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public int getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }

    public Set<Player> getTeamMembers() {
        return teamMembers;
    }

    public TeamPolicy getPolicy() {
        return policy;
    }

    public boolean isInTournament() {
        return isInTournament;
    }

    public Player getCreator() {
        return creator;
    }

    public void setCreator(Player creator) {
        this.creator = creator;
    }

    public void setPolicy(TeamPolicy policy) {
        this.policy = policy;
    }

    public void setInTournament(boolean inTournament) {
        isInTournament = inTournament;
    }

    public void setTeamMembers(Set<Player> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public boolean isFull() {
        return this.teamMembers.size()==this.maxNumberOfPlayers;
    }

    public void addMember(Player player) {
        this.teamMembers.add(player);
    }
}
