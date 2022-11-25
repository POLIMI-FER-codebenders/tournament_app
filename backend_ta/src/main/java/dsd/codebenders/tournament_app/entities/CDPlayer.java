package dsd.codebenders.tournament_app.entities;

import javax.persistence.*;

@Entity
@Table(name = "cd_player")
public class CDPlayer {

    @Id
    private Long userId;
    private String username;
    private String server;
    private String token;

    @ManyToOne
    @JoinColumn(name = "ID_player")
    private Player realPlayer;

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
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

    public void setServer(String server) {
        this.server = server;
    }

    public void setRealPlayer(Player realPlayer) {
        this.realPlayer = realPlayer;
    }
}
