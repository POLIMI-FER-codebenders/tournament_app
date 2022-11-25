package dsd.codebenders.tournament_app.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dsd.codebenders.tournament_app.serializers.TeamIDAndNameSerializer;
import dsd.codebenders.tournament_app.serializers.TournamentIDSerializer;

@Entity
@Table(name = "tournament_score")
@JsonIgnoreProperties({"id"})
public class TournamentScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @ManyToOne
    @JoinColumn(name = "ID_team")
    @JsonSerialize(using = TeamIDAndNameSerializer.class)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    @JsonSerialize(using = TournamentIDSerializer.class)
    private Tournament tournament;

    private Integer score = 0;

    @Column(name = "league_points")
    private Integer leaguePoints = 0;

    public TournamentScore() {
    }

    public TournamentScore(Tournament tournament, Team team) {
        this.tournament = tournament;
        this.team = team;
    }

    public Long getID() {
        return ID;
    }

    public Team getTeam() {
        return team;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public Integer getScore() {
        return score;
    }

    public Integer getLeaguePoints() {
        return leaguePoints;
    }
}
