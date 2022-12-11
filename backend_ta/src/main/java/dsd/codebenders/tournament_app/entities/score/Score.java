package dsd.codebenders.tournament_app.entities.score;

public abstract class Score {
    private String username;
    private Long userId;
    private Integer points;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getPoints() {
        return points;
    }
}
