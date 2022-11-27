package dsd.codebenders.tournament_app.dao;

import dsd.codebenders.tournament_app.entities.Match;
import dsd.codebenders.tournament_app.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE Match m SET m.status = 'STARTED', m.server = ?2, m.gameId = ?3 WHERE m.ID = ?1")
    void setCreatedMatchById(Long ID, String server, int cdID);

    @Query("SELECT m FROM Match m WHERE m.ID IN" +
            "(SELECT m.ID FROM Player p JOIN p.team t JOIN t.gamesAsAttackers m WHERE p = ?1 AND m.status = 'STARTED')" +
            "OR m.ID IN" +
            "(SELECT m.ID FROM Player p JOIN p.team t JOIN t.gamesAsDefenders m WHERE p = ?1 AND m.status = 'STARTED')")
    Match findStartedMatchByPlayer(Player player);

}
