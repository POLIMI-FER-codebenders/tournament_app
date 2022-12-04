package dsd.codebenders.tournament_app.entities;

import dsd.codebenders.tournament_app.entities.utils.TeamPolicy;
import dsd.codebenders.tournament_app.responses.TeamResponse;

import javax.persistence.*;
import java.util.List;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "max_number_of_players", nullable = false)
    private int maxNumberOfPlayers;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_creator", nullable = false)
    private Player creator;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private Set<Player> teamMembers;

    @Column(name = "policy", nullable = false)
    @Enumerated(EnumType.STRING)
    private TeamPolicy policy;

    @Column(name = "in_tournament", nullable = false)
    private boolean isInTournament;

    @Column(name = "date_of_creation", nullable = false)
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

    @OneToMany(mappedBy = "attackersTeam", fetch = FetchType.LAZY)
    private List<Match> gamesAsAttackers;

    @OneToMany(mappedBy = "attackersTeam", fetch = FetchType.LAZY)
    private List<Match> gamesAsDefenders;

    public LocalDate getDateOfCreation() {
        return dateOfCreation;
    }

    public boolean getIsInTournament() {
        return isInTournament;
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

    public List<Match> getGamesAsAttackers() {
        return gamesAsAttackers;
    }

    public List<Match> getGamesAsDefenders() {
        return gamesAsDefenders;
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
