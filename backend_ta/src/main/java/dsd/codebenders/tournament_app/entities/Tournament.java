package dsd.codebenders.tournament_app.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dsd.codebenders.tournament_app.entities.utils.MatchType;
import dsd.codebenders.tournament_app.entities.utils.TournamentStatus;
import dsd.codebenders.tournament_app.entities.utils.TournamentType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LeagueTournament.class, name = "LEAGUE"),
        @JsonSubTypes.Type(value = KnockoutTournament.class, name = "KNOCKOUT")
})
@Entity
@Table(name = "tournament")
@DiscriminatorColumn(name = "type")
@JsonIgnoreProperties(value = {"id", "tournamentScores", "creator", "currentRound", "status"}, allowGetters = true)
public abstract class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    private String name;

    @Column(name = "number_of_teams")
    private Integer numberOfTeams;

    @Column(name = "team_size")
    private Integer teamSize;

    @ManyToOne
    @JoinColumn(name = "ID_creator")
    private Player creator;

    @Enumerated(EnumType.STRING)
    @Column(insertable = false, updatable = false)
    private TournamentType type;

    @Column(name = "match_type")
    @Enumerated(EnumType.STRING)
    private MatchType matchType;

    @Enumerated(EnumType.STRING)
    private TournamentStatus status = TournamentStatus.TEAMS_JOINING;

    @Column(name = "current_round")
    private Integer currentRound;

    @OneToMany(mappedBy = "tournament")
    private List<TournamentScore> tournamentScores = new ArrayList<>();

    public Long getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public Integer getNumberOfTeams() {
        return numberOfTeams;
    }

    public Integer getTeamSize() {
        return teamSize;
    }

    public Player getCreator() {
        return creator;
    }

    public void setCreator(Player creator) {
        this.creator = creator;
    }

    public TournamentType getType() {
        return type;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    public List<TournamentScore> getTournamentScores() {
        return tournamentScores;
    }

    public Integer getCurrentRound() {
        return currentRound;
    }
}
