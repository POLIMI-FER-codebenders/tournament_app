package dsd.codebenders.tournament_app.entities.streaming;

import dsd.codebenders.tournament_app.entities.score.MultiplayerScoreboard;

public class CDEvent {

    private Integer userId;
    private String message;
    private String type;
    private Long timestamp;

    private MultiplayerScoreboard scoreboard;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public MultiplayerScoreboard getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(MultiplayerScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

}
