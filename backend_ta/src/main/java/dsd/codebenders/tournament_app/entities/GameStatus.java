package dsd.codebenders.tournament_app.entities;

import dsd.codebenders.tournament_app.entities.score.Scoreboard;
import dsd.codebenders.tournament_app.entities.utils.GameState;

public class GameStatus {
    private Integer classId;
    private GameState state;
    //private List<MutantInfo> mutants; //TODO
    //private List<TestInfo> tests; //TODO
    private Scoreboard scoreboard;

    public Integer getClassId() {
        return classId;
    }

    public GameState getState() {
        return state;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}
