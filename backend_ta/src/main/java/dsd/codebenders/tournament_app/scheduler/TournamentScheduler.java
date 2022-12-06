package dsd.codebenders.tournament_app.scheduler;

import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.Tournament;
import dsd.codebenders.tournament_app.services.MatchService;
import dsd.codebenders.tournament_app.services.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class TournamentScheduler extends ThreadPoolTaskScheduler {

    @Value("${tournament-app.tournament-delay.round-start:15}")
    private int roundStartDelay;
    @Value("${tournament-app.tournament-delay.phase-two:15}")
    private int phaseTwoDelay;
    @Value("${tournament-app.tournament-delay.phase-three:15}")
    private int phaseThreeDelay;
    @Value("${tournament-app.tournament-delay.match-ending:15}")
    private int matchEndingDelay;

    private final TournamentService tournamentService;
    private final MatchService matchService;

    @Autowired
    public TournamentScheduler(TournamentService tournamentService, MatchService matchService) {
        this.tournamentService = tournamentService;
        this.matchService = matchService;
    }

    @Async
    public void prepareRoundAndStartMatches(Tournament tournament) {
        // TODO: schedule the round
        List<Match> matches = tournamentService.getMatchesInCurrentRound(tournament);
        Date roundStart = addSeconds(new Date(), roundStartDelay);
        for(Match m: matches) {
            matchService.setStartDate(m, roundStart);
            schedule(new CreateAndStartMatchesTask(matchService, m), roundStart);
            schedule(new DisableTestsAndMutantsTask(matchService, m), addSeconds(roundStart, phaseTwoDelay));
            schedule(new DisableEquivalenceClaimsTask(matchService, m), addSeconds(roundStart, phaseThreeDelay));
            schedule(new EndMatchTask(this, matchService, m), addSeconds(roundStart, matchEndingDelay));
        }
    }

    private Date addSeconds(Date date, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime();
    }

}
