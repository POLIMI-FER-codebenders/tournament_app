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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "cd_game_class", uniqueConstraints = @UniqueConstraint(columnNames = {"ID_game_class", "id_server"}))
@JsonIgnoreProperties(value = "server", allowSetters = true)
public class CDGameClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    @Column(name = "cd_class_id", nullable = false)
    private int classId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_game_class", nullable = false)
    private GameClass realClass;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_server", nullable = false)
    private Server server;

    public Long getID() {
        return ID;
    }

    public int getClassId() {
        return classId;
    }

    public GameClass getRealClass() {
        return realClass;
    }

    public void setRealClass(GameClass realClass) {
        this.realClass = realClass;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}
