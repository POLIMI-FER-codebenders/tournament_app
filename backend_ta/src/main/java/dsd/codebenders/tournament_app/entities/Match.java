package dsd.codebenders.tournament_app.entities;

import javax.persistence.*;
@Entity
@Table(name = "match")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private String status;
    private String server;
    private int gameId;

    @ManyToOne
    @JoinColumn(name = "ID_attackers_team")
    private Team attackersTeam;

    @ManyToOne
    @JoinColumn(name = "ID_defenders_team")
    private Team defendersTeam;

    public Long getID() {
        return ID;
    }

    public String getStatus() {
        return status;
    }

    public String getServer() {
        return server;
    }

    public int getGameId() {
        return gameId;
    }

    public Team getAttackersTeam() {
        return attackersTeam;
    }

    public Team getDefendersTeam() {
        return defendersTeam;
    }
}
