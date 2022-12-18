package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Server;
import dsd.codebenders.tournament_app.entities.score.MultiplayerScoreboard;
import dsd.codebenders.tournament_app.entities.streaming.EventType;
import dsd.codebenders.tournament_app.entities.streaming.CDEventList;
import dsd.codebenders.tournament_app.entities.streaming.CDEvent;
import dsd.codebenders.tournament_app.entities.streaming.StreamingEvent;
import dsd.codebenders.tournament_app.tasks.SendEventTask;
import dsd.codebenders.tournament_app.utils.DateUtility;
import dsd.codebenders.tournament_app.utils.HTTPRequestsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.*;

@Service
public class StreamingService {

    @Value("${tournament-app.streaming.updates-delay:10000}")
    private int streamingUpdatesDelay;
    private final MatchService matchService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ServerService serverService;
    private final CDPlayerService cdPlayerService;
    private final ThreadPoolTaskScheduler scheduler;

    private Long lastEventTimestamp;

    @Autowired
    public StreamingService(MatchService matchService, SimpMessagingTemplate simpMessagingTemplate, ServerService serverService, CDPlayerService cdPlayerService, ThreadPoolTaskScheduler scheduler) {
        this.matchService = matchService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.serverService = serverService;
        this.cdPlayerService = cdPlayerService;
        this.scheduler = scheduler;
        this.lastEventTimestamp = null;
    }

    @Scheduled(fixedDelayString = "${tournament-app.streaming.updates-delay:10000}")
    @Async
    public void getUpdates() {
        if(lastEventTimestamp == null) {
            lastEventTimestamp = DateUtility.toSeconds(DateUtility.addSeconds(new Date(), - streamingUpdatesDelay / 1000));
        }
        Map<String, String> query = new HashMap<>();
        query.put("fromTimestamp", Long.toString(lastEventTimestamp + 1));
        List<Server> servers = serverService.getAllActiveServers();
        List<StreamingEvent> events = new ArrayList<>();
        for(Server s: servers) {
            CDEventList cdEventList;
            do {
                try {
                    cdEventList = HTTPRequestsSender.sendGetRequest(s, "/admin/api/events", query, CDEventList.class);
                    events.addAll(convertToStreamingEventList(cdEventList, s));
                    query.put("fromTimestamp", Long.toString(lastEventTimestamp + 1));
                } catch (RestClientException e) {
                    System.err.println("Impossible to retrieve streaming events from server " + s.getAddress());
                    cdEventList = null;
                }
            } while(cdEventList != null && cdEventList.getHasMore());
        }
        events.sort((o1, o2) -> (int) (o1.getTimestamp() - o2.getTimestamp()));
        for(StreamingEvent e : events) {
            Long lastScheduledEventTimestamp = e.getMatch().getLastEventTimestamp();
            Long lastScheduledEventSentTime = e.getMatch().getLastEventSentTime();
            Date newEventDate = new Date();
            if(lastScheduledEventSentTime == null) {
                scheduler.execute(new SendEventTask(simpMessagingTemplate, e));
            } else {
                long sendDelay = lastScheduledEventSentTime + (e.getTimestamp() - lastScheduledEventTimestamp) - DateUtility.toSeconds(newEventDate);
                if(sendDelay > 0) {
                    newEventDate = DateUtility.addSeconds(newEventDate, (int) sendDelay);
                    scheduler.schedule(new SendEventTask(simpMessagingTemplate, e), newEventDate);
                } else {
                    scheduler.execute(new SendEventTask(simpMessagingTemplate, e));
                }
            }
            matchService.setLastEventSent(e.getMatch(), e.getTimestamp(), DateUtility.toSeconds(newEventDate));
        }
    }

    private List<StreamingEvent> convertToStreamingEventList(CDEventList cdEventList, Server server) {
        List<StreamingEvent> streamingEvents = new ArrayList<>();
        for(Integer id: cdEventList.getEvents().keySet()) {
            Match match = matchService.getMatchByCDGameIdAnsServer(id, server);
            List<CDEvent> cdEvents = cdEventList.getEvents().get(id);
            for(CDEvent e: cdEvents) {
                if(e.getUserId() != null) {
                    Player player = cdPlayerService.findByUserIdAndServer(e.getUserId(), server).get().getRealPlayer();
                    try {
                        streamingEvents.add(new StreamingEvent(match, player.getUsername(), EventType.valueOf(e.getType()), e.getTimestamp()));
                    } catch (IllegalArgumentException ignored) { }
                } else {
                    try {
                        streamingEvents.add(new StreamingEvent(match, EventType.valueOf(e.getType()), e.getTimestamp()));
                    } catch (IllegalArgumentException ignored) { }
                }
                MultiplayerScoreboard scoreboard = e.getMultiplayerScoreboard();
                if(scoreboard != null) {
                    streamingEvents.add(new StreamingEvent(match, EventType.SCORE_UPDATE, e.getTimestamp(),
                            scoreboard.getAttackersTotal().getPoints(), scoreboard.getDefendersTotal().getPoints()));
                }
                if(e.getTimestamp() > lastEventTimestamp) {
                    lastEventTimestamp = e.getTimestamp();
                }
            }
        }
        return streamingEvents;
    }

}
