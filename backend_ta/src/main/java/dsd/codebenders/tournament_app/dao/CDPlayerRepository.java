package dsd.codebenders.tournament_app.dao;

import dsd.codebenders.tournament_app.entities.CDPlayer;
import dsd.codebenders.tournament_app.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CDPlayerRepository extends JpaRepository<CDPlayer, Long> {

    @Query("SELECT cdp FROM CDPlayer cdp WHERE cdp.realPlayer = ?1 AND cdp.server = ?2")
    CDPlayer findByRealPlayerAndServer(Player realPlayer, String server);

}
