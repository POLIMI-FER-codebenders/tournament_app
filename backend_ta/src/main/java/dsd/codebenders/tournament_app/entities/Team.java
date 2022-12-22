package dsd.codebenders.tournament_app.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.fasterxml.jackson.annotation.JsonFormat;
import dsd.codebenders.tournament_app.entities.utils.TeamPolicy;
import dsd.codebenders.tournament_app.responses.TeamResponse;

@Entity
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    @Column(nullable = false, unique = true, columnDefinition = "Varchar(255)")
    private String name;
    @Column(name = "max_number_of_players", nullable = false)
    private int maxNumberOfPlayers;
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_creator", nullable = false)
    private Player creator;
    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private Set<Player> teamMembers = new HashSet<>();
    @Column(name = "policy", nullable = false)
    @Enumerated(EnumType.STRING)
    private TeamPolicy policy;
    @Column(name = "in_tournament", nullable = false)
    private boolean isInTournament;
    @Column(name = "date_of_creation", nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateOfCreation;
    @OneToMany(mappedBy = "attackersTeam", fetch = FetchType.LAZY)
    private List<Match> gamesAsAttackers;
    @OneToMany(mappedBy = "attackersTeam", fetch = FetchType.LAZY)
    private List<Match> gamesAsDefenders;

    public Team() {
    }

    public Team(String name, int maxNumberOfPlayers, Player creator, TeamPolicy policy, boolean isInTournament, LocalDate dateOfCreation) {
        this.name = name;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.creator = creator;
        this.policy = policy;
        this.isInTournament = isInTournament;
        this.dateOfCreation = dateOfCreation;
    }

    public Team(String name, int maxNumberOfPlayers , TeamPolicy policy) {
        this.name = name;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.policy = policy;
    }

    public TeamResponse serialize() {
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

    public LocalDate getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(LocalDate dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
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

    public void setMaxNumberOfPlayers(int maxNumberOfPlayers) {
        this.maxNumberOfPlayers = maxNumberOfPlayers;
    }

    public Set<Player> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(Set<Player> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public void addTeamMember(Player teamMember) {
        this.teamMembers.add(teamMember);
    }

    public void removeTeamMember(Player teamMember) {
        this.teamMembers.remove(teamMember);
    }

    public TeamPolicy getPolicy() {
        return policy;
    }

    public void setPolicy(TeamPolicy policy) {
        this.policy = policy;
    }

    public boolean isInTournament() { //TODO this could check if a TournamentScore exists for this team and an active tournament, unless we're doing this to cache the information then it's fine
        return isInTournament;
    }

    public void setInTournament(boolean inTournament) {
        isInTournament = inTournament;
    }

    public Player getCreator() {
        return creator;
    }

    public void setCreator(Player creator) {
        this.creator = creator;
    }

    public List<Match> getGamesAsAttackers() {
        return gamesAsAttackers;
    }

    public List<Match> getGamesAsDefenders() {
        return gamesAsDefenders;
    }

    public boolean isFull() {
        return this.teamMembers.size() == this.maxNumberOfPlayers;
    }

    public boolean isPlayerInTeam(Player player){
        return this.teamMembers.contains(player);
    }


    @Override
    public String toString(){
        return "Team: " + this.getID() + " " + this.name + " " + this.maxNumberOfPlayers + " " + this.teamMembers;
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
