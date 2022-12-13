package dsd.codebenders.tournament_app.dao;

import dsd.codebenders.tournament_app.entities.GameClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameClassRepository extends JpaRepository<GameClass, Long> {
    public boolean existsByFilename(String filename);

}
