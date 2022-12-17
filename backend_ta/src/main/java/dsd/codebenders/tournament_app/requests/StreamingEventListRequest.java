package dsd.codebenders.tournament_app.requests;

import dsd.codebenders.tournament_app.entities.score.MeleeScoreboard;
import dsd.codebenders.tournament_app.entities.score.MultiplayerScoreboard;

import java.util.List;
import java.util.Map;

public class StreamingEventListRequest {

    private Map<Integer, List<StreamingEventRequest>> events;
    private Map<Integer, MultiplayerScoreboard> multiplayerScoreboards;
    private Map<Integer, MeleeScoreboard> meleeScoreboards;
    private Boolean hasMore;

    public Map<Integer, List<StreamingEventRequest>> getEvents() {
        return events;
    }

    public void setEvents(Map<Integer, List<StreamingEventRequest>> events) {
        this.events = events;
    }

    public Map<Integer, MultiplayerScoreboard> getMultiplayerScoreboards() {
        return multiplayerScoreboards;
    }

    public void setMultiplayerScoreboards(Map<Integer, MultiplayerScoreboard> multiplayerScoreboards) {
        this.multiplayerScoreboards = multiplayerScoreboards;
    }

    public Map<Integer, MeleeScoreboard> getMeleeScoreboards() {
        return meleeScoreboards;
    }

    public void setMeleeScoreboards(Map<Integer, MeleeScoreboard> meleeScoreboards) {
        this.meleeScoreboards = meleeScoreboards;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }

}
