package dsd.codebenders.tournament_app.dao;

import dsd.codebenders.tournament_app.entities.Server;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerRepository extends JpaRepository<Server,Long> {

}
