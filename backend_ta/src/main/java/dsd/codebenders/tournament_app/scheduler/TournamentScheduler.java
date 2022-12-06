package dsd.codebenders.tournament_app.scheduler;

import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.Tournament;
import dsd.codebenders.tournament_app.entities.utils.MatchStatus;
import dsd.codebenders.tournament_app.entities.utils.TournamentStatus;
import dsd.codebenders.tournament_app.errors.MatchCreationException;
import dsd.codebenders.tournament_app.services.MatchService;
import dsd.codebenders.tournament_app.services.TournamentService;
import dsd.codebenders.tournament_app.utils.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

public class TournamentScheduler extends ThreadPoolTaskScheduler {

    @Value("${tournament-app.tournament-delay.phase-two:15}")
    private int phaseTwoDelay;
    @Value("${tournament-app.tournament-delay.phase-three:15}")
    private int phaseThreeDelay;
    @Value("${tournament-app.tournament-delay.match-ending:15}")
    private int matchEndingDelay;

    private final TournamentService tournamentService;
    private final MatchService matchService;

    public TournamentScheduler(TournamentService tournamentService, MatchService matchService) {
        this.tournamentService = tournamentService;
        this.matchService = matchService;
    }
    
    public Tournament prepareRoundAndStartMatches(Tournament tournament) {
        try {
            tournament = tournamentService.tryAdvance(tournament);
        } catch (MatchCreationException e) {
            tournament = tournamentService.forceTournamentEnd(tournament);
            return tournament;
        }
        if(tournament.getStatus() != TournamentStatus.IN_PROGRESS) {
            return tournament;
        }
        List<Match> matches = tournamentService.getMatchesInCurrentRound(tournament);
        for(Match m: matches) {
            if(m.getStatus() == MatchStatus.CREATED) {
                Date roundStart = m.getStartDate();
                schedule(new StartMatchTask(m, matchService, this), roundStart);
                schedule(new DisableTestsAndMutantsTask(m, matchService, this), DateUtility.addSeconds(roundStart, phaseTwoDelay));
                schedule(new DisableEquivalenceClaimsTask(m, matchService, this), DateUtility.addSeconds(roundStart, phaseThreeDelay));
                schedule(new EndMatchTask(m, matchService, this), DateUtility.addSeconds(roundStart, matchEndingDelay));
            }
        }
        return tournament;
    }

}
