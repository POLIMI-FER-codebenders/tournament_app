package dsd.codebenders.tournament_app.requests;

import java.util.List;
import java.util.Map;

public class StreamingEventListRequest {

    private Map<Integer, List<StreamingEventRequest>> events;
    private Boolean hasMore;

    public Map<Integer, List<StreamingEventRequest>> getEvents() {
        return events;
    }

    public void setEvents(Map<Integer, List<StreamingEventRequest>> events) {
        this.events = events;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }

}
