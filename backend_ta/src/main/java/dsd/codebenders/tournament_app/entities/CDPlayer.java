package dsd.codebenders.tournament_app.entities;

import javax.persistence.*;

@Entity
@Table(name = "cd_player", uniqueConstraints = @UniqueConstraint(columnNames = {"ID_player", "ID_server"}))
public class CDPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String token;
    @Column(name = "user_ID", nullable = false)
    private int userId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_player", nullable = false)
    private Player realPlayer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_server", nullable = false)
    private Server server;

    public Long getID() {
        return ID;
    }

    public String getUsername() {
        return username;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public String getToken() {
        return token;
    }

    public Player getRealPlayer() {
        return realPlayer;
    }

    public void setRealPlayer(Player realPlayer) {
        this.realPlayer = realPlayer;
    }

    public int getUserId() {
        return userId;
    }
}
