package dsd.codebenders.tournament_app.requests;

public class GameRequest {

    private final int classId;
    private final TeamRequest[] teams;
    private final GameSettingsRequest settings;
    private final String returnUrl;


    public GameRequest(int classId, TeamRequest[] teams, GameSettingsRequest settings, String returnUrl) {
        this.classId = classId;
        this.teams = teams;
        this.settings = settings;
        this.returnUrl = returnUrl;
    }

    public int getClassId() {
        return classId;
    }

    public TeamRequest[] getTeams() {
        return teams;
    }

    public GameSettingsRequest getSettings() {
        return settings;
    }

    public String getReturnUrl() {
        return returnUrl;
    }
}
