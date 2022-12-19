package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.entities.CDPlayer;
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
            Long lastScheduledEventTimestamp = e.getMatch().getLastScheduledEventTimestamp();
            Long lastScheduledEventSendingTime = e.getMatch().getLastScheduledEventSendingTime();
            Date newEventDate = new Date();
            if(lastScheduledEventSendingTime == null) {
                scheduler.execute(new SendEventTask(matchService, simpMessagingTemplate, e));
            } else {
                long sendDelay = lastScheduledEventSendingTime + (e.getTimestamp() - lastScheduledEventTimestamp) - DateUtility.toSeconds(newEventDate);
                if(sendDelay > 0) {
                    newEventDate = DateUtility.addSeconds(newEventDate, (int) sendDelay);
                    scheduler.schedule(new SendEventTask(matchService, simpMessagingTemplate, e), newEventDate);
                } else {
                    scheduler.execute(new SendEventTask(matchService, simpMessagingTemplate, e));
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
            for(CDEvent cdEvent: cdEvents) {
                EventType eventType;
                try {
                    eventType = EventType.valueOf(cdEvent.getType());
                } catch (IllegalArgumentException e) {
                    System.err.println("Received unsupported event " + cdEvent.getType());
                    continue;
                }
                if(eventType == EventType.GAME_CREATED || eventType == EventType.GAME_STARTED || eventType == EventType.GAME_GRACE_ONE
                        || eventType == EventType.GAME_GRACE_TWO || eventType == EventType.GAME_FINISHED) {
                    streamingEvents.add(new StreamingEvent(match, eventType, cdEvent.getTimestamp()));
                } else {
                    Optional <CDPlayer> cdPlayer = cdPlayerService.findByUserIdAndServer(cdEvent.getUserId(), server);
                    if(cdPlayer.isPresent()) {
                        Player player = cdPlayer.get().getRealPlayer();
                        streamingEvents.add(new StreamingEvent(match, player.getUsername(), eventType, cdEvent.getTimestamp()));
                    } else {
                        System.err.println("Unable to retrieve CD player " + cdEvent.getUserId() + " for event " + cdEvent.getType());
                        continue;
                    }
                }
                MultiplayerScoreboard scoreboard = cdEvent.getMultiplayerScoreboard();
                if(scoreboard != null) {
                    streamingEvents.add(new StreamingEvent(match, EventType.SCORE_UPDATE, cdEvent.getTimestamp(),
                            scoreboard.getAttackersTotal().getPoints(), scoreboard.getDefendersTotal().getPoints()));
                }
                if(cdEvent.getTimestamp() > lastEventTimestamp) {
                    lastEventTimestamp = cdEvent.getTimestamp();
                }
            }
        }
        return streamingEvents;
    }

}
