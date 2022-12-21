package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.TournamentScoreRepository;
import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.Tournament;
import dsd.codebenders.tournament_app.entities.utils.MatchStatus;
import dsd.codebenders.tournament_app.entities.utils.TournamentStatus;
import dsd.codebenders.tournament_app.errors.MatchCreationException;
import dsd.codebenders.tournament_app.tasks.DisableEquivalenceClaimsTask;
import dsd.codebenders.tournament_app.tasks.DisableTestsAndMutantsTask;
import dsd.codebenders.tournament_app.tasks.EndMatchTask;
import dsd.codebenders.tournament_app.tasks.StartMatchTask;
import dsd.codebenders.tournament_app.utils.DateUtility;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Date;
import java.util.List;

public class TournamentSchedulerService extends ThreadPoolTaskScheduler {

    private final int phaseTwoDelay;
    private final int phaseThreeDelay;
    private final int matchEndingDelay;
    private final TournamentService tournamentService;
    private final MatchService matchService;

    public TournamentSchedulerService(int phaseTwoDelay, int phaseThreeDelay, int matchEndingDelay, TournamentService tournamentService, MatchService matchService) {
        this.phaseTwoDelay = phaseTwoDelay;
        this.phaseThreeDelay = phaseThreeDelay;
        this.matchEndingDelay = matchEndingDelay;
        this.tournamentService = tournamentService;
        this.matchService = matchService;
    }

    public Tournament prepareRoundAndStartMatches(Tournament tournament) {
        try {
            tournament = tournamentService.tryAdvance(tournament);
        } catch (MatchCreationException e) {
            System.err.println("ERROR: Tournament " + tournament.getID() + " failed while creating matches");
            tournament = tournamentService.forceTournamentEnd(tournament);
            return tournament;
        }
        if(tournament.getStatus() != TournamentStatus.IN_PROGRESS) {
            return tournament;
        }
        List<Match> matches = matchService.getMatchesByTournamentAndRoundNumber(tournament, tournament.getCurrentRound());
        for(Match m: matches) {
            if(m.getStatus() == MatchStatus.CREATED) {
                Date roundStart = m.getStartDate();
                schedule(new StartMatchTask(m, matchService, this), roundStart);
                schedule(new DisableTestsAndMutantsTask(m, matchService, this), DateUtility.addSeconds(roundStart, phaseTwoDelay));
                schedule(new DisableEquivalenceClaimsTask(m, matchService, this), DateUtility.addSeconds(roundStart, phaseThreeDelay));
                schedule(new EndMatchTask(m, matchService, tournamentService, this), DateUtility.addSeconds(roundStart, matchEndingDelay));
            }
        }
        return tournament;
    }

}
