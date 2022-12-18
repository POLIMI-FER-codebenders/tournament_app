package dsd.codebenders.tournament_app.dao;

import java.util.List;

import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Server;
import dsd.codebenders.tournament_app.entities.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    @Query("SELECT m FROM Match m WHERE m.ID IN"
            + "(SELECT m.ID FROM Match m JOIN m.attackersTeam t JOIN t.teamMembers p WHERE p = ?1 AND m.status <> 'ENDED' AND m.status <> 'FAILED')")
    Match findCreatedMatchByAttacker(Player player);

    @Query("SELECT m FROM Match m WHERE m.ID IN"
            + "(SELECT m.ID FROM Match m JOIN m.defendersTeam t JOIN t.teamMembers p WHERE p = ?1 AND m.status <> 'ENDED' AND m.status <> 'FAILED')")
    Match findCreatedMatchByDefender(Player player);

    List<Match> findByTournamentAndRoundNumber(Tournament tournament, Integer roundNumber);

    @Query("SELECT m FROM Match m WHERE m.status = 'IN_PHASE_ONE' OR m.status = 'IN_PHASE_TWO' OR m.status = 'IN_PHASE_THREE'")
    List<Match> findOngoingMatches();

    Match findByGameIdAndServer(Integer gameId, Server server);

    @Transactional
    @Modifying
    @Query("UPDATE Match m SET m.streamedAttackersScore = ?2, m.streamedDefendersScore = ?3, m.lastSentScoreEventTimestamp = ?4 " +
            "WHERE m.ID = ?1 AND m.lastSentScoreEventTimestamp <= ?4")
    void updateLastScoreEvent(long id, int attackersScore, int defendersScore, long timestamp);

}
