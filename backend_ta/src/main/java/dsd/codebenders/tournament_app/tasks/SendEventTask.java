package dsd.codebenders.tournament_app.tasks;

import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.event.StreamingEvent;

import org.springframework.messaging.simp.SimpMessagingTemplate;

public class SendEventTask implements Runnable {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Match match;
    private final StreamingEvent event;

    public SendEventTask(SimpMessagingTemplate simpMessagingTemplate, Match match, StreamingEvent event) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.match = match;
        this.event = event;
    }

    @Override
    public void run() {
        String queueName = "/live/" + match.getID();
        System.out.println("send to " + queueName);
        simpMessagingTemplate.convertAndSend(queueName, "ping");
        // set game timer to event time  in the DB
    }

}
