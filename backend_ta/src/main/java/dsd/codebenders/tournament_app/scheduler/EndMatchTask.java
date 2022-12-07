package dsd.codebenders.tournament_app.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.Server;
import dsd.codebenders.tournament_app.entities.utils.MatchStatus;
import dsd.codebenders.tournament_app.requests.GameIdRequest;
import dsd.codebenders.tournament_app.services.MatchService;
import dsd.codebenders.tournament_app.utils.HTTPRequestsSender;
import org.springframework.web.client.RestClientException;

public class EndMatchTask implements Runnable {

    private final Match match;
    private final MatchService matchService;
    private final TournamentScheduler tournamentScheduler;

    public EndMatchTask(Match match, MatchService matchService, TournamentScheduler tournamentScheduler) {
        this.match = match;
        this.matchService = matchService;
        this.tournamentScheduler = tournamentScheduler;
    }

    @Override
    public void run() {
        if(match.getStatus() == MatchStatus.IN_PHASE_THREE) {
            Server server = match.getServer();
            try {
                HTTPRequestsSender.sendPostRequest(server, "/admin/api/game/end", new GameIdRequest(match), void.class);
            } catch (RestClientException | JsonProcessingException e) {
                if(matchService.setFailedMatchAndCheckRoundEnding(match)) {
                    System.err.println("ERROR: Match " + match.getID() + " failed while ending");
                    tournamentScheduler.prepareRoundAndStartMatches(match.getTournament());
                }
                return;
            }
            if (matchService.endMatchAndCheckRoundEnding(match)) {
                // TODO: get scores and set winner
                tournamentScheduler.prepareRoundAndStartMatches(match.getTournament());
            }
        }
    }

}
