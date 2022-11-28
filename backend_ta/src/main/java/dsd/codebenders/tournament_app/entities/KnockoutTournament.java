package dsd.codebenders.tournament_app.entities;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("KNOCKOUT")
public class KnockoutTournament extends Tournament {
    @Override
    public int getNumberOfRounds() {
        return (int) Math.floor(Math.log(numberOfTeams) / Math.log(2));
    }

    @Override
    public List<List<Long>> scheduleMatches(List<Long> allTeamIds, List<Long> winningTeamIds) {
        Long[][] ret = new Long[winningTeamIds.size() / 2][2];
        int n = 0;
        for (long id : winningTeamIds) {
            ret[(int) Math.floor((double) n / 2)][n % 2] = id;
            n++;
        }
        return Arrays.stream(ret).map(m -> Arrays.stream(m).collect(Collectors.toList())).toList();
    }
}
