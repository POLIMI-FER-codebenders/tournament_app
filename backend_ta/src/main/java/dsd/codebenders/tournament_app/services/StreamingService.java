package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.event.StreamingEvent;
import dsd.codebenders.tournament_app.tasks.SendEventTask;
import dsd.codebenders.tournament_app.utils.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class StreamingService extends ThreadPoolTaskScheduler {

    private final MatchService matchService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public StreamingService(MatchService matchService, SimpMessagingTemplate simpMessagingTemplate) {
        this.matchService = matchService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void addViewer(Match match) {
        // open web socket
        // increase number of users following match
    }

    public void removeViewer(Match match) {
        // decrease number of users following match
        // close the socket
    }

    @Scheduled(fixedDelay = 10000)
    public void getUpdates() {
        List<Match> matches = matchService.getOngoingMatches();
        for(Match m : matches) {
            // get updates for the game:
            StreamingEvent event = new StreamingEvent();
            Date date = DateUtility.addSeconds(new Date(), 10);
            schedule(new SendEventTask(simpMessagingTemplate, m, event), date);
        }
    }

}
