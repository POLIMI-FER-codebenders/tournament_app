package dsd.codebenders.tournament_app.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.Server;
import dsd.codebenders.tournament_app.entities.utils.MatchStatus;
import dsd.codebenders.tournament_app.services.MatchService;
import dsd.codebenders.tournament_app.utils.HTTPRequestsSender;
import org.springframework.web.client.RestClientException;

public class DisableEquivalenceClaimsTask implements Runnable {

    private final Match match;
    private final MatchService matchService;
    private final TournamentScheduler tournamentScheduler;

    public DisableEquivalenceClaimsTask(Match match, MatchService matchService, TournamentScheduler tournamentScheduler) {
        this.match = match;
        this.matchService = matchService;
        this.tournamentScheduler = tournamentScheduler;
    }

    @Override
    public void run() {
        if(match.getStatus() == MatchStatus.STARTED) {
            Server server = match.getServer();
            try {
                HTTPRequestsSender.sendPostRequest(server, "/admin/api/game/disable-claims", "{gameId: " + match.getGameId() + "}", void.class);
            } catch (RestClientException | JsonProcessingException e) {
                if(matchService.setFailedMatchAndCheckRoundEnding(match)) {
                    tournamentScheduler.prepareRoundAndStartMatches(match.getTournament());
                }
            }
        }
    }

}
