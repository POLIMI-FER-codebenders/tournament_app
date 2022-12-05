package dsd.codebenders.tournament_app.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.Server;
import dsd.codebenders.tournament_app.services.MatchService;
import dsd.codebenders.tournament_app.utils.HTTPRequestsSender;

public class DisableTestsAndMutantsTask implements Runnable {

    private final MatchService matchService;
    private final Match match;

    public DisableTestsAndMutantsTask(MatchService matchService, Match match) {
        this.matchService = matchService;
        this.match = match;
    }

    @Override
    public void run() {
        Server server = match.getServer();
        try {
            HTTPRequestsSender.sendPostRequest(server, "/admin/api/game/disable-uploads", "{gameId: " + match.getGameId() + "}", void.class);
        } catch (JsonProcessingException e) {
            matchService.setFailedMatch(match);
        }
    }

}
