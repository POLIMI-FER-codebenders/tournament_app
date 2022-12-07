package dsd.codebenders.tournament_app.entities.score;

import java.util.ArrayList;
import java.util.List;

public class MeleeScoreboard extends Scoreboard {
    List<MeleeScore> players = new ArrayList<>();

    public List<MeleeScore> getPlayers() {
        return players;
    }
}
