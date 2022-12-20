package dsd.codebenders.tournament_app.dao;

import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeamRepository extends JpaRepository<Team, Long> {
    public boolean existsTeamByName(String name);

    @Query("SELECT tn FROM Team tn WHERE tn.name = ?1")
    Team findByName(String name);
}
