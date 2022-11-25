package dsd.codebenders.tournament_app.entities;

import javax.persistence.*;

@Entity
@Table(name = "cd_player")
public class CDPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private String server;
    private String token;

    @ManyToOne
    @JoinColumn(name = "ID_player")
    private Player realPlayer;

    public Long getID() {
        return ID;
    }

    public String getServer() {
        return server;
    }

    public String getToken() {
        return token;
    }

    public Player getRealPlayer() {
        return realPlayer;
    }

}
