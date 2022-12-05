package dsd.codebenders.tournament_app.dao;

import dsd.codebenders.tournament_app.entities.CDPlayer;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Server;
import dsd.codebenders.tournament_app.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CDPlayerRepository extends JpaRepository<CDPlayer, Long> {

    @Query("SELECT cdp FROM CDPlayer cdp WHERE cdp.realPlayer = ?1 AND cdp.server = ?2")
    CDPlayer findByRealPlayerAndServer(Player realPlayer, Server server);
    @Query("SELECT cdp FROM Team t JOIN t.teamMembers p JOIN p.codeDefendersPlayers cdp WHERE t = ?1 AND cdp.server = ?2")
    List<CDPlayer> findByTeamAndServer(Team team, Server server);


}
