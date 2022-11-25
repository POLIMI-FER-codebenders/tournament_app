package dsd.codebenders.tournament_app.dao;

import java.util.List;

import dsd.codebenders.tournament_app.entities.Tournament;
import dsd.codebenders.tournament_app.entities.utils.TournamentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    List<Tournament> findAllByType(TournamentType type);

    List<Tournament> findByTournamentScores_Team_ID(Long ID);
}
