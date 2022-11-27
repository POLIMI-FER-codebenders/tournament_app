package dsd.codebenders.tournament_app.dao;

import dsd.codebenders.tournament_app.entities.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MatchRepository extends JpaRepository<Match, Long> {

    @Modifying
    @Query("UPDATE Match m SET m.status = 'STARTED', m.server = ?2, m.gameId = ?3 WHERE m.ID = ?1")
    void setCreatedMatchById(Long ID, String server, int cdID);

}
