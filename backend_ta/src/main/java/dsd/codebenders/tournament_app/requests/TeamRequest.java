package dsd.codebenders.tournament_app.requests;

public class TeamRequest {

    private final int[] userIds;
    private final String role;

    public TeamRequest(int[] userIds, String role) {
        this.userIds = userIds;
        this.role = role;
    }

    public int[] getUserIds() {
        return userIds;
    }

    public String getRole() {
        return role;
    }
}
