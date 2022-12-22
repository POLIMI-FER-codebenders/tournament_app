package dsd.codebenders.tournament_app.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.Server;
import dsd.codebenders.tournament_app.entities.utils.MatchStatus;
import dsd.codebenders.tournament_app.requests.GameIdRequest;
import dsd.codebenders.tournament_app.services.MatchService;
import dsd.codebenders.tournament_app.services.TournamentSchedulerService;
import dsd.codebenders.tournament_app.utils.HTTPRequestsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;

public class DisableTestsAndMutantsTask implements Runnable {

    private final Match match;
    private final MatchService matchService;
    private final TournamentSchedulerService tournamentSchedulerService;
    private final Logger logger = LoggerFactory.getLogger(DisableTestsAndMutantsTask.class);

    public DisableTestsAndMutantsTask(Match match, MatchService matchService, TournamentSchedulerService tournamentSchedulerService) {
        this.match = match;
        this.matchService = matchService;
        this.tournamentSchedulerService = tournamentSchedulerService;
    }

    @Override
    public void run() {
        if(match.getStatus() == MatchStatus.IN_PHASE_ONE) {
            Server server = match.getServer();
            try {
                HTTPRequestsSender.sendPostRequest(server, "/admin/api/game/disable-uploads", new GameIdRequest(match), void.class);
                logger.info("Disabled tests and mutants on match " + match.getID() + ", continuing to the next phase");
                matchService.goToNextPhase(match);
            } catch (RestClientException | JsonProcessingException e) {
                System.err.println("ERROR: Match " + match.getID() + " failed while disabling tests and mutants");
                if(matchService.setFailedMatchAndCheckRoundEnding(match)) {
                    tournamentSchedulerService.prepareRoundAndStartMatches(match.getTournament());
                }
            }
        }
    }

}
