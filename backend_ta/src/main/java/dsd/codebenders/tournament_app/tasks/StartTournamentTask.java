package dsd.codebenders.tournament_app.tasks;

import dsd.codebenders.tournament_app.entities.Tournament;
import dsd.codebenders.tournament_app.services.TournamentSchedulerService;

public class StartTournamentTask implements Runnable {

    private final Tournament tournament;
    private final TournamentSchedulerService tournamentSchedulerService;

    public StartTournamentTask(Tournament tournament, TournamentSchedulerService tournamentSchedulerService) {
        this.tournament = tournament;
        this.tournamentSchedulerService = tournamentSchedulerService;
    }

    @Override
    public void run() {
        tournamentSchedulerService.prepareRoundAndStartMatches(tournament);
    }

}
