package dsd.codebenders.tournament_app.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "cd_player", uniqueConstraints = @UniqueConstraint(columnNames = {"ID_player", "server"}))
public class CDPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String server;
    @JsonIgnore
    private String token;
    @Column(name = "user_ID", nullable = false)
    private int userId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_player", nullable = false)
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

    public void setServer(String server) {
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
