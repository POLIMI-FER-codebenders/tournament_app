package dsd.codebenders.tournament_app.entities.score;

import java.util.ArrayList;
import java.util.List;

public class MultiplayerScoreboard extends Scoreboard {
    List<AttackerScore> attackers = new ArrayList<>();
    AttackerScore attackersTotal;
    List<DefenderScore> defenders = new ArrayList<>();
    DefenderScore defendersTotal;

    public List<AttackerScore> getAttackers() {
        return attackers;
    }

    public AttackerScore getAttackersTotal() {
        return attackersTotal;
    }

    public List<DefenderScore> getDefenders() {
        return defenders;
    }

    public DefenderScore getDefendersTotal() {
        return defendersTotal;
    }
}
