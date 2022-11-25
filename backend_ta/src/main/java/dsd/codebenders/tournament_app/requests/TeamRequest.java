package dsd.codebenders.tournament_app.requests;

public class TeamRequest {

    private final Long[] userIds;
    private final String role;

    public TeamRequest(Long[] userIds, String role) {
        this.userIds = userIds;
        this.role = role;
    }
}
