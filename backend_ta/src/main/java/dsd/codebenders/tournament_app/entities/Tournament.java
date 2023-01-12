package dsd.codebenders.tournament_app.entities;

import java.util.*;

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

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true, include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({@JsonSubTypes.Type(value = LeagueTournament.class, name = "LEAGUE"), @JsonSubTypes.Type(value = KnockoutTournament.class, name = "KNOCKOUT")})
@Entity
@Table(name = "tournament")
@DiscriminatorColumn(name = "type")
@JsonIgnoreProperties(value = {"id", "tournamentScores", "creator", "currentRound", "status", "nextRoundStartTime", "winningTeam", "matches"}, allowGetters = true)
public abstract class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long ID;

    @Column(nullable = false, columnDefinition = "Varchar(255)")
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

    @Column(name = "start_date")
    protected Date startDate;

    @OneToMany(mappedBy = "tournament", fetch = FetchType.EAGER)
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

    public void setName(String name) {
        this.name = name;
    }

    public void setNumberOfTeams(Integer numberOfTeams) {
        this.numberOfTeams = numberOfTeams;
    }

    public void setTeamSize(Integer teamSize) {
        this.teamSize = teamSize;
    }

    public void setType(TournamentType type) {
        this.type = type;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public void setCurrentRound(Integer currentRound) {
        this.currentRound = currentRound;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
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

    public void addRoundClassChoice(RoundClassChoice roundClassChoice) {
        this.roundClassChoiceList.add(roundClassChoice);
    }

    public abstract int getNumberOfRounds();

    public abstract List<List<Long>> scheduleMatches(List<Long> allTeamIds, List<Long> winningTeamIds);
}
