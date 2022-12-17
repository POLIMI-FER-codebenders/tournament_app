package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Server;
import dsd.codebenders.tournament_app.entities.score.MultiplayerScoreboard;
import dsd.codebenders.tournament_app.requests.StreamingEventListRequest;
import dsd.codebenders.tournament_app.requests.StreamingEventRequest;
import dsd.codebenders.tournament_app.responses.StreamingEventResponse;
import dsd.codebenders.tournament_app.tasks.SendEventTask;
import dsd.codebenders.tournament_app.utils.DateUtility;
import dsd.codebenders.tournament_app.utils.HTTPRequestsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.*;

@Service
public class StreamingService {

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

    @Scheduled(fixedDelay = 15000)
    @Async
    public void getUpdates() {
        if(lastEventTimestamp == null) {
            lastEventTimestamp = DateUtility.toSeconds(DateUtility.addSeconds(new Date(), -15));
        }
        long timestamp = lastEventTimestamp + 1;
        Map<String, String> query = new HashMap<>();
        query.put("fromTimestamp", Long.toString(timestamp));
        List<Server> servers = serverService.getAllActiveServers();
        List<StreamingEventResponse> events = new ArrayList<>();
        for(Server s: servers) {
            StreamingEventListRequest request;
            do {
                try {
                    request = HTTPRequestsSender.sendGetRequest(s, "/admin/api/events", query, StreamingEventListRequest.class);
                    events.addAll(convertToStreamingEventResponseList(request, lastEventTimestamp, s));

                } catch (RestClientException e) {
                    System.err.println("Impossible to retrieve streaming events from server " + s.getAddress());
                    request = null;
                }
                // TODO: update timestamp
            } while(request != null && request.getHasMore());
        }
        events.sort((o1, o2) -> (int) (o1.getTimestamp() - o2.getTimestamp()));
        for(StreamingEventResponse e : events) {
            //TODO: determine right delay
            Date date = DateUtility.addSeconds(new Date(), 10);
            scheduler.schedule(new SendEventTask(matchService, simpMessagingTemplate, e), date);
        }
    }

    private List<StreamingEventResponse> convertToStreamingEventResponseList(StreamingEventListRequest request, Long timestamp, Server server) {
        // TODO: move score events based on Riccardo's update of API
        List<StreamingEventResponse> responses = new ArrayList<>();
        for(Integer id: request.getMultiplayerScoreboards().keySet()) {
            Match match = matchService.getMatchByCDGameIdAnsServer(id, server);
            MultiplayerScoreboard scoreboard = request.getMultiplayerScoreboards().get(id);
            responses.add(new StreamingEventResponse(match, "SCORE_UPDATE", timestamp,
                    scoreboard.getAttackersTotal().getPoints(), scoreboard.getDefendersTotal().getPoints()));
        }
        for(Integer id: request.getEvents().keySet()) {
            Match match = matchService.getMatchByCDGameIdAnsServer(id, server);
            List<StreamingEventRequest> events = request.getEvents().get(id);
            for(StreamingEventRequest e: events) {
                if(e.getUserId() != null) {
                    Player player = cdPlayerService.findByUserIdAndServer(e.getUserId(), server).get().getRealPlayer();
                    responses.add(new StreamingEventResponse(match, player.getUsername(), e.getType(), e.getTimestamp()));
                } else {
                    responses.add(new StreamingEventResponse(match, e.getType(), e.getTimestamp()));
                }
            }
        }
        return responses;
    }

}
