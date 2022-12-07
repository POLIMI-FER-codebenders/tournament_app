package dsd.codebenders.tournament_app.entities.score;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({@JsonSubTypes.Type(value = MultiplayerScoreboard.class), @JsonSubTypes.Type(value = MeleeScoreboard.class)})
public abstract class Scoreboard {
}
