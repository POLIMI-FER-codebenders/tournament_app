package dsd.codebenders.tournament_app.tasks;

import dsd.codebenders.tournament_app.entities.streaming.EventType;
import dsd.codebenders.tournament_app.entities.streaming.StreamingEvent;
import dsd.codebenders.tournament_app.services.MatchService;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class SendEventTask implements Runnable {

    private final MatchService matchService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final StreamingEvent event;

    public SendEventTask(MatchService matchService, SimpMessagingTemplate simpMessagingTemplate, StreamingEvent event) {
        this.matchService = matchService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.event = event;
    }

    @Override
    public void run() {
        long matchId = event.getMatch().getID();
        String queueName = "/live/" + matchId;
        System.out.println("send to " + queueName);
        simpMessagingTemplate.convertAndSend(queueName, event);
        if(event.getType() == EventType.SCORE_UPDATE) {
            matchService.updateLastScoreEvent(matchId, event.getAttackersScore(), event.getDefendersScore(), event.getTimestamp());
        }
    }

}
