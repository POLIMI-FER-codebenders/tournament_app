package dsd.codebenders.tournament_app.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "server")
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(nullable = false, unique = true)
    private String address;

    @Column(name = "admin_token", nullable = false)
    private String adminToken;

    @Column(name = "is_active", nullable = false, columnDefinition="Boolean default '1'")
    private Boolean isActive = false;

    @OneToMany(mappedBy = "server", fetch = FetchType.LAZY)
    private List<CDPlayer> cdPlayers;

    @OneToMany(mappedBy = "server", fetch = FetchType.LAZY)
    private List<Match> matches;

    public Server() {

    }

    public Server(String address, String adminToken) {
        this.address = address;
        this.adminToken = adminToken;
        this.isActive = true;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public Long getID() {
        return ID;
    }

    public String getAddress() {
        return address;
    }

    public String getAdminToken() {
        return adminToken;
    }

    public Boolean isActive() {
        return isActive;
    }

    public List<CDPlayer> getCdPlayers() {
        return cdPlayers;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setAdminToken(String adminToken) {
        this.adminToken = adminToken;
    }
}
