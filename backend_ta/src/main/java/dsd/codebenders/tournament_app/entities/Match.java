package dsd.codebenders.tournament_app.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import dsd.codebenders.tournament_app.entities.utils.MatchStatus;

@Entity
@Table(name = "game")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status;
    private String server;
    @Column(name = "game_ID", nullable = false)
    private Integer gameId;
    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;
    @ManyToOne(optional = false)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_attackers_team", nullable = false)
    private Team attackersTeam;
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_defenders_team", nullable = false)
    private Team defendersTeam;
    @ManyToOne
    @JoinColumn(name = "winning_team_id")
    private Team winningTeam;

    public Match() {
    }

    public Match(Team attackersTeam, Team defendersTeam, Integer roundNumber, Tournament tournament) {
        this.attackersTeam = attackersTeam;
        this.defendersTeam = defendersTeam;
        this.roundNumber = roundNumber;
        this.tournament = tournament;
    }

    public Long getID() {
        return ID;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public String getServer() {
        return server;
    }

    public int getGameId() {
        return gameId;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public Team getAttackersTeam() {
        return attackersTeam;
    }

    public Team getDefendersTeam() {
        return defendersTeam;
    }

    public Team getWinningTeam() {
        return winningTeam;
    }

    public void setWinningTeam(Team winningTeam) {
        this.winningTeam = winningTeam;
    }

}
