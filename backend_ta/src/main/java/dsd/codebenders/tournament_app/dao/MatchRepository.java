package dsd.codebenders.tournament_app.dao;

import java.util.List;

import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MatchRepository extends JpaRepository<Match, Long> {
    @Query("SELECT m FROM Match m WHERE m.ID IN" + "(SELECT m.ID FROM Player p JOIN p.team t JOIN t.gamesAsAttackers m WHERE p = ?1 AND m.status <> 'ENDED' AND m.status <> 'FAILED')" + "OR m.ID IN" +
            "(SELECT m.ID FROM Player p JOIN p.team t JOIN t.gamesAsDefenders m WHERE p = ?1 AND m.status <> 'ENDED' AND m.status <> 'FAILED')")
    Match findStartedMatchByPlayer(Player player);

    List<Match> findByTournamentAndRoundNumber(Tournament tournament, Integer roundNumber);
}
