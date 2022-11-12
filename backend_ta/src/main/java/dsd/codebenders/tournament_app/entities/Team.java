package dsd.codebenders.tournament_app.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "team")
@JsonIgnoreProperties({ "creator" })
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    private String name;

    @Column(name = "max_number_of_players")
    private int maxNumberOfPlayers;

    @ManyToOne
    @JoinColumn(name = "ID_creator")
    private Player creator;

    public Long getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public int getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }

    public Player getCreator() {
        return creator;
    }

    public void setCreator(Player creator) {
        this.creator = creator;
    }
}
