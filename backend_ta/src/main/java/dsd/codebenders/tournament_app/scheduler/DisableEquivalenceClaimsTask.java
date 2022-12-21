package dsd.codebenders.tournament_app.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.Server;
import dsd.codebenders.tournament_app.entities.utils.MatchStatus;
import dsd.codebenders.tournament_app.requests.GameIdRequest;
import dsd.codebenders.tournament_app.services.MatchService;
import dsd.codebenders.tournament_app.utils.HTTPRequestsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;

public class DisableEquivalenceClaimsTask implements Runnable {

    private final Match match;
    private final MatchService matchService;
    private final TournamentScheduler tournamentScheduler;
    private final Logger logger = LoggerFactory.getLogger(DisableEquivalenceClaimsTask.class);

    public DisableEquivalenceClaimsTask(Match match, MatchService matchService, TournamentScheduler tournamentScheduler) {
        this.match = match;
        this.matchService = matchService;
        this.tournamentScheduler = tournamentScheduler;
    }

    @Override
    public void run() {
        if(match.getStatus() == MatchStatus.IN_PHASE_TWO) {
            Server server = match.getServer();
            try {
                HTTPRequestsSender.sendPostRequest(server, "/admin/api/game/disable-claims", new GameIdRequest(match), void.class);
                logger.info("Disabled equivalence claims on match " + match.getID() + ", continuing to the next phase");
                matchService.goToNextPhase(match);
            } catch (RestClientException | JsonProcessingException e) {
                System.err.println("ERROR: Match " + match.getID() + " failed while disabling equivalence claims");
                if(matchService.setFailedMatchAndCheckRoundEnding(match)) {
                    tournamentScheduler.prepareRoundAndStartMatches(match.getTournament());
                }
            }
        }
    }

}
