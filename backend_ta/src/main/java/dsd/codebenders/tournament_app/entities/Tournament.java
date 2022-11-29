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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dsd.codebenders.tournament_app.entities.utils.MatchType;
import dsd.codebenders.tournament_app.entities.utils.TournamentStatus;
import dsd.codebenders.tournament_app.entities.utils.TournamentType;
import dsd.codebenders.tournament_app.serializers.PlayerIDAndNameSerializer;
import dsd.codebenders.tournament_app.serializers.TeamIDAndNameSerializer;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = LeagueTournament.class, name = "LEAGUE"), @JsonSubTypes.Type(value = KnockoutTournament.class, name = "KNOCKOUT")})
@Entity
@Table(name = "tournament")
@DiscriminatorColumn(name = "type")
@JsonIgnoreProperties(value = {"id", "tournamentScores", "creator", "currentRound", "status"}, allowGetters = true)
public abstract class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long ID;

    @Column(nullable = false)
    protected String name;

    @Column(name = "number_of_teams", nullable = false)
    protected Integer numberOfTeams;

    @Column(name = "team_size", nullable = false)
    protected Integer teamSize;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_creator", nullable = false)
    @JsonSerialize(using = PlayerIDAndNameSerializer.class)
    protected Player creator;

    @Column(nullable = false, insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    protected TournamentType type;

    @Column(name = "match_type", nullable = false)
    @Enumerated(EnumType.STRING)
    protected MatchType matchType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    protected TournamentStatus status = TournamentStatus.TEAMS_JOINING;

    @Column(name = "current_round")
    protected Integer currentRound = 0;

    @OneToMany(mappedBy = "tournament")
    protected List<TournamentScore> tournamentScores = new ArrayList<>();

    @OneToMany(mappedBy = "tournament")
    protected List<Match> matches = new ArrayList<>();

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

    public List<Match> getMatches() {
        return matches;
    }

    public Integer getCurrentRound() {
        return currentRound;
    }

    public Integer incrementCurrentRound() {
        return currentRound++;
    }

    public abstract int getNumberOfRounds();

    public abstract List<List<Long>> scheduleMatches(List<Long> allTeamIds, List<Long> winningTeamIds);
}
