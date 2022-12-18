package dsd.codebenders.tournament_app.tasks;

import dsd.codebenders.tournament_app.entities.streaming.StreamingEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class SendEventTask implements Runnable {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final StreamingEvent event;

    public SendEventTask(SimpMessagingTemplate simpMessagingTemplate, StreamingEvent event) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.event = event;
    }

    @Override
    public void run() {
        String queueName = "/live/" + event.getMatch().getID();
        System.out.println("send to " + queueName);
        simpMessagingTemplate.convertAndSend(queueName, event);
    }

}
