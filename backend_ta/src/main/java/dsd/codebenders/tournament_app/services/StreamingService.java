package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.event.StreamingEvent;
import dsd.codebenders.tournament_app.tasks.SendEventTask;
import dsd.codebenders.tournament_app.utils.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class StreamingService {

    private final MatchService matchService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ThreadPoolTaskScheduler scheduler;

    @Autowired
    public StreamingService(MatchService matchService, SimpMessagingTemplate simpMessagingTemplate, ThreadPoolTaskScheduler scheduler) {
        this.matchService = matchService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.scheduler = scheduler;
    }

    @Scheduled(fixedDelay = 10000)
    @Async
    public void getUpdates() {
        System.out.println("Get Updates");
        List<Match> matches = matchService.getOngoingMatches();
        for(Match m : matches) {
            // get updates for the game:
            StreamingEvent event = new StreamingEvent();
            Date date = DateUtility.addSeconds(new Date(), 10);
            scheduler.schedule(new SendEventTask(simpMessagingTemplate, m, event), date);
        }
    }

}
