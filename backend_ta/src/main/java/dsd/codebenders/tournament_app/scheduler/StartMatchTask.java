package dsd.codebenders.tournament_app.scheduler;

import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.errors.MatchCreationException;
import dsd.codebenders.tournament_app.services.MatchService;

public class StartMatchTask implements Runnable {

    private final Match match;
    private final MatchService matchService;
    private final TournamentScheduler tournamentScheduler;

    public StartMatchTask(Match match, MatchService matchService, TournamentScheduler tournamentScheduler) {
        this.match = match;
        this.matchService = matchService;
        this.tournamentScheduler = tournamentScheduler;
    }

    @Override
    public void run() {
        try {
            matchService.startMatchOnCD(match);
        } catch (MatchCreationException e) {
            if(matchService.setFailedMatchAndCheckRoundEnding(match)) {
                tournamentScheduler.prepareRoundAndStartMatches(match.getTournament());
            }
        }
    }
}
