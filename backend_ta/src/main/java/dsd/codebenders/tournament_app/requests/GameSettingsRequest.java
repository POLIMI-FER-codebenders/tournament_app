package dsd.codebenders.tournament_app.requests;

public class GameSettingsRequest {

    private final String gameType;
    private final String gameLevel;
    private final String mutantValidatorLevel;
    private final int maxAssertionsPerTest;
    private final int autoEquivalenceThreshold;

    public GameSettingsRequest(String gameType, String gameLevel, String mutantValidatorLevel, int maxAssertionsPerTest, int autoEquivalenceThreshold) {
        this.gameType = gameType;
        this.gameLevel = gameLevel;
        this.mutantValidatorLevel = mutantValidatorLevel;
        this.maxAssertionsPerTest = maxAssertionsPerTest;
        this.autoEquivalenceThreshold = autoEquivalenceThreshold;
    }
}
