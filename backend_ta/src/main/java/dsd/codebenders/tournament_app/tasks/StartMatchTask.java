package dsd.codebenders.tournament_app.tasks;

import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.errors.MatchCreationException;
import dsd.codebenders.tournament_app.services.MatchService;
import dsd.codebenders.tournament_app.services.TournamentSchedulerService;

public class StartMatchTask implements Runnable {

    private final Match match;
    private final MatchService matchService;
    private final TournamentSchedulerService tournamentSchedulerService;

    public StartMatchTask(Match match, MatchService matchService, TournamentSchedulerService tournamentSchedulerService) {
        this.match = match;
        this.matchService = matchService;
        this.tournamentSchedulerService = tournamentSchedulerService;
    }

    @Override
    public void run() {
        try {
            matchService.startMatchOnCD(match);
        } catch (MatchCreationException e) {
            System.err.println("ERROR: Match " + match.getID() + " failed while starting");
            if(matchService.setFailedMatchAndCheckRoundEnding(match)) {
                tournamentSchedulerService.prepareRoundAndStartMatches(match.getTournament());
            }
        }
    }
}
