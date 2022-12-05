package dsd.codebenders.tournament_app.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.Server;
import dsd.codebenders.tournament_app.services.MatchService;
import dsd.codebenders.tournament_app.utils.HTTPRequestsSender;

public class EndMatchTask implements Runnable {

    private final TournamentScheduler tournamentScheduler;
    private final MatchService matchService;
    private final Match match;

    public EndMatchTask(TournamentScheduler tournamentScheduler, MatchService matchService, Match match) {
        this.tournamentScheduler = tournamentScheduler;
        this.matchService = matchService;
        this.match = match;
    }

    @Override
    public void run() {
        Server server = match.getServer();
        try {
            HTTPRequestsSender.sendPostRequest(server, "/admin/api/game/end", "{gameId: " + match.getGameId() + "}", void.class);
        } catch (JsonProcessingException e) {
            matchService.setFailedMatch(match);
        }
        //TODO: set game as ENDED and check if all games of current round have ended
        if(true) {
            tournamentScheduler.prepareRoundAndStartMatches(match.getTournament());
        }
    }

}
