package dsd.codebenders.tournament_app.dao;

import dsd.codebenders.tournament_app.entities.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Long> {
}
