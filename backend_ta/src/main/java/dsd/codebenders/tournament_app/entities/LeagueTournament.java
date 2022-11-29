package dsd.codebenders.tournament_app.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("LEAGUE")
public class LeagueTournament extends Tournament {
    private static int lowerOdd(int n) {
        return n - (1 - n % 2);
    }

    @Override
    public int getNumberOfRounds() {
        return lowerOdd(numberOfTeams);
    }

    @Override
    public List<List<Long>> scheduleMatches(List<Long> allTeamIds, List<Long> winningTeamIds) {
        List<int[]> out = new ArrayList<>();
        int lowerOdd = lowerOdd(numberOfTeams);
        for (int numberOfMatch = numberOfTeams % 2; numberOfMatch <= Math.ceil((float) numberOfTeams / 2) - 1; numberOfMatch++) {
            out.add(new int[] {numberOfMatch == 0 ? 0 : ((currentRound + numberOfMatch - 2) % lowerOdd + 1 - numberOfTeams % 2),
                    (currentRound + lowerOdd - numberOfMatch - 2) % lowerOdd + 1 - numberOfTeams % 2});
        }
        return out.stream().map(m -> Arrays.stream(m).mapToObj(allTeamIds::get).toList()).toList();
    }

}
