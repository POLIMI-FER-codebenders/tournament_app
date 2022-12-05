package dsd.codebenders.tournament_app.scheduler;

import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.errors.MatchCreationException;
import dsd.codebenders.tournament_app.services.MatchService;

public class CreateAndStartMatchesTask implements Runnable {

    private final MatchService matchService;
    private final Match match;

    public CreateAndStartMatchesTask(MatchService matchService, Match match) {
        this.matchService = matchService;
        this.match = match;
    }

    @Override
    public void run() {
        try {
            matchService.createAndStartMatch(match);
        } catch (MatchCreationException e) {
            matchService.setFailedMatch(match);
        }
    }
}
