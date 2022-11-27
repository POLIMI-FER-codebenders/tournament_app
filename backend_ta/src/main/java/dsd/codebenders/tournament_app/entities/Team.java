package dsd.codebenders.tournament_app.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import dsd.codebenders.tournament_app.entities.utils.TeamPolicy;
import dsd.codebenders.tournament_app.responses.TeamResponse;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "team")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
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

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private Set<Player> teamMembers;

    @Column(name = "policy")
    @Enumerated(EnumType.STRING)
    private TeamPolicy policy;

    @Column(name = "in_tournament")
    private boolean isInTournament;

    @Column(name = "date_of_creation")
    private LocalDate dateOfCreation;

    public TeamResponse serialize(){
        return new TeamResponse(
                this.ID,
                this.name,
                this.maxNumberOfPlayers,
                this.teamMembers.stream().map(Player::serialize).collect(Collectors.toSet()),
                this.policy,
                this.isInTournament,
                this.dateOfCreation,
                this.isFull()
                );
    }

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

    public boolean isInTournament() { //TODO this could check if a TournamentScore exists for this team and an active tournament, unless we're doing this to cache the information then it's fine
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

    public void setDateOfCreation(LocalDate dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public boolean isFull() {
        return this.teamMembers.size()==this.maxNumberOfPlayers;
    }

    public void addMember(Player player) {
        this.teamMembers.add(player);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Team team = (Team) o;
        return ID.equals(team.ID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }
}
