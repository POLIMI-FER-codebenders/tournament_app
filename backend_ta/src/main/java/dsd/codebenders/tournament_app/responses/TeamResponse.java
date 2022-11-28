package dsd.codebenders.tournament_app.responses;

import dsd.codebenders.tournament_app.entities.utils.TeamPolicy;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class TeamResponse {
    private Long id;
    private String name;
    private Integer maxNumberOfPlayers;
    private Set<TeamMemberResponse> members;
    private TeamPolicy policy;
    private boolean isInTournament;
    private LocalDate dateOfCreation;
    private boolean isFull;

    public TeamResponse(Long id, String name, Integer maxNumberOfPlayers, Set<TeamMemberResponse> members, TeamPolicy policy, boolean isInTournament, LocalDate dateOfCreation, boolean isFull) {
        this.id = id;
        this.name = name;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.members = members;
        this.policy = policy;
        this.isInTournament = isInTournament;
        this.dateOfCreation = dateOfCreation;
        this.isFull = isFull;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }

    public void setMaxNumberOfPlayers(Integer maxNumberOfPlayers) {
        this.maxNumberOfPlayers = maxNumberOfPlayers;
    }

    public Set<TeamMemberResponse> getMembers() {
        return members;
    }

    public void setMembers(Set<TeamMemberResponse> members) {
        this.members = members;
    }

    public TeamPolicy getPolicy() {
        return policy;
    }

    public void setPolicy(TeamPolicy policy) {
        this.policy = policy;
    }

    public boolean isInTournament() {
        return isInTournament;
    }

    public void setInTournament(boolean inTournament) {
        isInTournament = inTournament;
    }

    public LocalDate getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(LocalDate dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public boolean isFull() {
        return isFull;
    }

    public void setFull(boolean full) {
        isFull = full;
    }
}
