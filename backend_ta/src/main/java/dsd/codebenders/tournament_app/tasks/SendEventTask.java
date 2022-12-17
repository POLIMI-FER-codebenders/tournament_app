package dsd.codebenders.tournament_app.tasks;

import dsd.codebenders.tournament_app.responses.StreamingEventResponse;
import dsd.codebenders.tournament_app.services.MatchService;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class SendEventTask implements Runnable {

    private final MatchService matchService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final StreamingEventResponse event;

    public SendEventTask(MatchService matchService, SimpMessagingTemplate simpMessagingTemplate, StreamingEventResponse event) {
        this.matchService = matchService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.event = event;
    }

    @Override
    public void run() {
        String queueName = "/live/" + event.getMatch().getID();
        System.out.println("send to " + queueName);
        simpMessagingTemplate.convertAndSend(queueName, event);
        matchService.setLastEventSentTimestamp(event.getMatch(), event.getTimestamp());
    }

}
