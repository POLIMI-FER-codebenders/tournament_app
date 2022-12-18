package dsd.codebenders.tournament_app.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.streaming.EventType;

public class StreamingEventResponse {

    @JsonIgnore
    private Match match;
    private String user;
    private EventType type;
    private Long timestamp;
    private Integer attackersScore;
    private Integer defendersScore;

    public StreamingEventResponse(Match match, String user, EventType type, Long timestamp) {
        this.match = match;
        this.user = user;
        this.type = type;
        this.timestamp = timestamp;
    }

    public StreamingEventResponse(Match match, EventType type, Long timestamp, Integer attackersScore, Integer defendersScore) {
        this.match = match;
        this.type = type;
        this.timestamp = timestamp;
        this.attackersScore = attackersScore;
        this.defendersScore = defendersScore;
    }

    public StreamingEventResponse(Match match, EventType type, Long timestamp) {
        this.match = match;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getAttackersScore() {
        return attackersScore;
    }

    public void setAttackersScore(Integer attackersScore) {
        this.attackersScore = attackersScore;
    }

    public Integer getDefendersScore() {
        return defendersScore;
    }

    public void setDefendersScore(Integer defendersScore) {
        this.defendersScore = defendersScore;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

}
