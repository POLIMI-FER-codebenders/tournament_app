package dsd.codebenders.tournament_app.dao;

import dsd.codebenders.tournament_app.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
