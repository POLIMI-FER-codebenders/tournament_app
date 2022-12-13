package dsd.codebenders.tournament_app.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dsd.codebenders.tournament_app.serializers.GameClassIDFilenameAuthorSerializer;
import dsd.codebenders.tournament_app.serializers.TournamentIDSerializer;

import javax.persistence.*;

@Entity
@Table(name = "round_class_choice", uniqueConstraints = @UniqueConstraint(columnNames = {"tournament_id", "round"}))
@JsonIgnoreProperties({"id"})
public class RoundClassChoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tournament_id")
    @JsonSerialize(using = TournamentIDSerializer.class)
    private Tournament tournament;

    @Column(nullable = false)
    private Integer round;

    @ManyToOne(optional = false)
    @JoinColumn(name = "class")
    @JsonSerialize(using = GameClassIDFilenameAuthorSerializer.class)
    private GameClass gameClass;

    public RoundClassChoice() {

    }

    public RoundClassChoice(Tournament tournament, Integer round, GameClass gameClass) {
        this.tournament = tournament;
        this.round = round;
        this.gameClass = gameClass;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public GameClass getGameClass() {
        return gameClass;
    }

    public void setGameClass(GameClass gameClass) {
        this.gameClass = gameClass;
    }
}
