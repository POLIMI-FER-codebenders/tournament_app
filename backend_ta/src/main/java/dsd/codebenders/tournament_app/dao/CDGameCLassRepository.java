package dsd.codebenders.tournament_app.dao;

import dsd.codebenders.tournament_app.entities.CDGameClass;
import dsd.codebenders.tournament_app.entities.GameClass;
import dsd.codebenders.tournament_app.entities.Server;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CDGameCLassRepository extends JpaRepository<CDGameClass, Long> {

    CDGameClass findByRealClassAndServer(GameClass gameClass, Server server);

}
