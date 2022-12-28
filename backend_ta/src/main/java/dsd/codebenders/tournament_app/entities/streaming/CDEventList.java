package dsd.codebenders.tournament_app.entities.streaming;

import java.util.List;
import java.util.Map;

public class CDEventList {

    private Map<Integer, List<CDEvent>> events;
    private Boolean hasMore;

    public Map<Integer, List<CDEvent>> getEvents() {
        return events;
    }

    public void setEvents(Map<Integer, List<CDEvent>> events) {
        this.events = events;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }

}
