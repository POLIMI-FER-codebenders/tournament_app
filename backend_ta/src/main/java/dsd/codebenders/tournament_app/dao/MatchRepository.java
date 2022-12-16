package dsd.codebenders.tournament_app.dao;

import java.util.List;

import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
