package dsd.codebenders.tournament_app.entities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

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
@JsonIgnoreProperties(value = {"id", "tournamentScores", "creator", "currentRound", "status", "nextRoundStartTime", "winningTeam", "matches"}, allowGetters = true)
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

    protected Date nextRoundStartTime = Date.from(Instant.now()); //TODO remove the default when we have scheduling

    @OneToMany(mappedBy = "tournament")
    protected List<TournamentScore> tournamentScores = new ArrayList<>();

    @OneToMany(mappedBy = "tournament")
    protected List<Match> matches = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "winning_team_id")
    @JsonSerialize(using = TeamIDAndNameSerializer.class)
    protected Team winningTeam;

    @OneToMany(mappedBy = "tournament")
    protected List<RoundClassChoice> roundClassChoiceList = new ArrayList<>();

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

    public Date getNextRoundStartTime() {
        return nextRoundStartTime;
    }

    public void setNextRoundStartTime(Date nextRoundStartTime) {
        this.nextRoundStartTime = nextRoundStartTime;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    public Team getWinningTeam() {
        return winningTeam;
    }

    public void setWinningTeam(Team winningTeam) {
        this.winningTeam = winningTeam;
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

    public List<RoundClassChoice> getRoundClassChoiceList() {
        return roundClassChoiceList;
    }

    public void setRoundClassChoiceList(List<RoundClassChoice> roundClassChoiceList) {
        this.roundClassChoiceList = roundClassChoiceList;
    }

    public abstract int getNumberOfRounds();

    public abstract List<List<Long>> scheduleMatches(List<Long> allTeamIds, List<Long> winningTeamIds);
}
