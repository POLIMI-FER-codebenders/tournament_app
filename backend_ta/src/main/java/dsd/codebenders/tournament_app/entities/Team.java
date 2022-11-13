package dsd.codebenders.tournament_app.entities;

import javax.persistence.*;

@Entity
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
}
