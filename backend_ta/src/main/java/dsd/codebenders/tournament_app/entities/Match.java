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
    private MatchStatus status;
    private String server;
    @Column(name = "game_ID", nullable = false)
    private Integer gameId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_attackers_team", nullable = false)
    private Team attackersTeam;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_defenders_team", nullable = false)
    private Team defendersTeam;

    public Integer getGameId() {
        return gameId;
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

    public Team getAttackersTeam() {
        return attackersTeam;
    }

    public Team getDefendersTeam() {
        return defendersTeam;
    }
}
