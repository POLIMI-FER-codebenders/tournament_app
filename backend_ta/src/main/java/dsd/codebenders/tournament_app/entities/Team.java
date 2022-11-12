package dsd.codebenders.tournament_app.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dsd.codebenders.tournament_app.entities.utils.TeamPolicy;

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

    @Column(name = "policy")
    @Enumerated(EnumType.STRING)
    private TeamPolicy policy;

    public Long getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public int getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }

    public TeamPolicy getPolicy() {
        return policy;
    }

    public Player getCreator() {
        return creator;
    }

    public void setCreator(Player creator) {
        this.creator = creator;
    }

    public void setPolicy(TeamPolicy policy) {
        this.policy = policy;
    }
}
