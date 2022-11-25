package dsd.codebenders.tournament_app.responses;

import dsd.codebenders.tournament_app.entities.utils.TeamRole;

public class TeamMemberResponse {
    private Long id;
    private String username;
    private TeamRole role;
    private Integer score;

    public TeamMemberResponse(Long id, String username, TeamRole role, Integer score) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public TeamRole getRole() {
        return role;
    }

    public void setRole(TeamRole role) {
        this.role = role;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
