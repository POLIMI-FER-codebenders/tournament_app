package dsd.codebenders.tournament_app.requests;

import dsd.codebenders.tournament_app.entities.Match;

public class GameIdRequest {

    private int gameId;

    public GameIdRequest(Match match) {
        gameId = match.getGameId();
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

}
