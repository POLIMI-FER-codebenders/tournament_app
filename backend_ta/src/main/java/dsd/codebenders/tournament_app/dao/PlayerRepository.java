package dsd.codebenders.tournament_app.dao;

import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Query("SELECT p FROM Player p WHERE p.username = ?1")
    Player findByUsername(String username);

    @Query("SELECT p FROM Player p WHERE p.email = ?1")
    Player findByEmail(String email);

    List<Player> findPlayersByTeam(Team team);

}
