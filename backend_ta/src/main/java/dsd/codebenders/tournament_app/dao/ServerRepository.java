package dsd.codebenders.tournament_app.dao;

import dsd.codebenders.tournament_app.entities.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ServerRepository extends JpaRepository<Server,Long> {

    @Query("SELECT s FROM Server s WHERE s.address = ?1")
    Server findByAddress(String address);

    @Transactional
    @Modifying
    @Query("UPDATE Server s SET s.adminToken = ?1 WHERE s = ?2")
    void updateToken(String token, Server server);

    @Transactional
    @Modifying
    @Query("UPDATE Server s SET s.isActive = 1 WHERE s = ?1")
    void updateAsActive(Server server);

    @Transactional
    @Modifying
    @Query("UPDATE Server s SET s.isActive = 0 WHERE s = ?1")
    void updateAsInactive(Server server);

}
