package dsd.codebenders.tournament_app.dao;

import java.util.List;

import dsd.codebenders.tournament_app.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByMaxNumberOfPlayersGreaterThanEqual(int maxNumberOfPlayers);

    public boolean existsTeamByName(String name);

    @Query("SELECT tn FROM Team tn WHERE tn.name = ?1")
    Team findByName(String name);
}
