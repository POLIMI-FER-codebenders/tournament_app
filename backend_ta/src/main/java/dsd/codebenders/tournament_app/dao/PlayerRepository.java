package dsd.codebenders.tournament_app.dao;

import dsd.codebenders.tournament_app.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    public Player findByUsername(String username);
}
