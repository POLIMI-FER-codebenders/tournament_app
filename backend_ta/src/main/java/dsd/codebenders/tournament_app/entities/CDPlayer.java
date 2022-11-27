package dsd.codebenders.tournament_app.entities;

import javax.persistence.*;

@Entity
@Table(name = "cd_player")
public class CDPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private String username;
    private String server;
    private String token;
    @Column(name="user_ID")
    private int userId;

    @ManyToOne
    @JoinColumn(name = "ID_player")
    private Player realPlayer;

    public Long getID() {
        return ID;
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

    public int getUserId() {
        return userId;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setRealPlayer(Player realPlayer) {
        this.realPlayer = realPlayer;
    }
}
