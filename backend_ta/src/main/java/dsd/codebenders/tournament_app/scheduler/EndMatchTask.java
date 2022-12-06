package dsd.codebenders.tournament_app.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.Server;
import dsd.codebenders.tournament_app.entities.utils.MatchStatus;
import dsd.codebenders.tournament_app.services.MatchService;
import dsd.codebenders.tournament_app.services.TournamentService;
import dsd.codebenders.tournament_app.utils.HTTPRequestsSender;

public class EndMatchTask implements Runnable {

    private final TournamentScheduler tournamentScheduler;
    private final TournamentService tournamentService;
    private final MatchService matchService;
    private final Match match;

    public EndMatchTask(TournamentScheduler tournamentScheduler, TournamentService tournamentService, MatchService matchService, Match match) {
        this.tournamentScheduler = tournamentScheduler;
        this.tournamentService = tournamentService;
        this.matchService = matchService;
        this.match = match;
    }

    @Override
    public void run() {
        if(match.getStatus() == MatchStatus.FAILED) {
            return;
        }
        Server server = match.getServer();
        try {
            HTTPRequestsSender.sendPostRequest(server, "/admin/api/game/end", "{gameId: " + match.getGameId() + "}", void.class);
        } catch (JsonProcessingException e) {
            matchService.setFailedMatch(match);
        }
        if(tournamentService.endMatchAndCheckRoundEnding(match)) {
            tournamentScheduler.prepareRoundAndStartMatches(match.getTournament());
        }
    }

}
