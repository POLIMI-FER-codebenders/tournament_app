package dsd.codebenders.tournament_app.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dsd.codebenders.tournament_app.entities.utils.MatchStatus;
import dsd.codebenders.tournament_app.serializers.TeamIDAndNameSerializer;
import dsd.codebenders.tournament_app.serializers.TournamentIDSerializer;

import java.util.Date;

@Entity
@Table(name = "game")
@JsonIgnoreProperties(value = {"server"}, allowSetters = true)
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MatchStatus status = MatchStatus.CREATED;
    @Column(name = "game_ID")
    private Integer gameId;
    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;
    @Column(name = "start_date", nullable = false)
    private Date startDate;
    @ManyToOne(optional = false)
    @JoinColumn(name = "tournament_id", nullable = false)
    @JsonSerialize(using = TournamentIDSerializer.class)
    private Tournament tournament;
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_attackers_team", nullable = false)
    @JsonSerialize(using = TeamIDAndNameSerializer.class)
    private Team attackersTeam;
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_defenders_team", nullable = false)
    @JsonSerialize(using = TeamIDAndNameSerializer.class)
    private Team defendersTeam;
    @ManyToOne
    @JoinColumn(name = "winning_team_id")
    @JsonSerialize(using = TeamIDAndNameSerializer.class)
    private Team winningTeam;
    @ManyToOne()
    @JoinColumn(name = "ID_server")
    private Server server;
    @JsonIgnore
    @Column(name = "last_scheduled_event_timestamp")
    private Long lastScheduledEventTimestamp;
    @JsonIgnore
    @Column(name = "last_sent_score_event_timestamp", columnDefinition = "Bigint default 0")
    private Long lastSentScoreEventTimestamp;
    @JsonIgnore
    @Column(name = "last_scheduled_event_sending_time")
    private Long lastScheduledEventSendingTime;
    @JsonIgnore
    @Column(name = "streamed_attackers_score", columnDefinition = "Int default 0")
    private Integer streamedAttackersScore;
    @JsonIgnore
    @Column(name = "streamed_defenders_score", columnDefinition = "Int default 0")
    private Integer streamedDefendersScore;

    public Match() {
        this.lastSentScoreEventTimestamp = 0L;
        this.streamedAttackersScore = 0;
        this.streamedDefendersScore = 0;
    }

    public Match(Team attackersTeam, Team defendersTeam, Integer roundNumber, Tournament tournament, Date startDate) {
        this.attackersTeam = attackersTeam;
        this.defendersTeam = defendersTeam;
        this.roundNumber = roundNumber;
        this.tournament = tournament;
        this.startDate = startDate;
        this.lastSentScoreEventTimestamp = 0L;
        this.streamedAttackersScore = 0;
        this.streamedDefendersScore = 0;
    }

    public Long getID() {
        return ID;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public Team getAttackersTeam() {
        return attackersTeam;
    }

    public Team getDefendersTeam() {
        return defendersTeam;
    }

    public Team getWinningTeam() {
        return winningTeam;
    }

    public void setWinningTeam(Team winningTeam) {
        this.winningTeam = winningTeam;
    }

    public Long getLastScheduledEventTimestamp() {
        return lastScheduledEventTimestamp;
    }

    public void setLastScheduledEventTimestamp(Long lastEventTimestamp) {
        this.lastScheduledEventTimestamp = lastEventTimestamp;
    }

    public Long getLastSentScoreEventTimestamp() {
        return lastSentScoreEventTimestamp;
    }

    public void setLastSentScoreEventTimestamp(Long lastSentEventTimestamp) {
        this.lastSentScoreEventTimestamp = lastSentEventTimestamp;
    }

    public Long getLastScheduledEventSendingTime() {
        return lastScheduledEventSendingTime;
    }

    public void setLastScheduledEventSendingTime(Long lastEventSentTime) {
        this.lastScheduledEventSendingTime = lastEventSentTime;
    }

    public Integer getStreamedAttackersScore() {
        return streamedAttackersScore;
    }

    public void setStreamedAttackersScore(Integer streamedAttackersScore) {
        this.streamedAttackersScore = streamedAttackersScore;
    }

    public Integer getStreamedDefendersScore() {
        return streamedDefendersScore;
    }

    public void setStreamedDefendersScore(Integer streamedDefendersScore) {
        this.streamedDefendersScore = streamedDefendersScore;
    }

}
