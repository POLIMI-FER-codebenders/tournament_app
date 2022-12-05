package dsd.codebenders.tournament_app.scheduler;

import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.Tournament;
import dsd.codebenders.tournament_app.services.MatchService;
import dsd.codebenders.tournament_app.services.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class TournamentScheduler extends ThreadPoolTaskScheduler {

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
        Date roundStart = addMinutes(new Date(), 60); //TODO: get current date and time
        for(Match m: matches) {
            schedule(new CreateAndStartMatchesTask(matchService, m), roundStart);
            schedule(new DisableTestsAndMutantsTask(matchService, m), addMinutes(roundStart, 120));
            schedule(new DisableEquivalenceClaimsTask(matchService, m), addMinutes(roundStart, 150));
            schedule(new EndMatchTask(this, matchService, m), addMinutes(roundStart, 180));
        }
    }

    private Date addMinutes(Date date, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

}
