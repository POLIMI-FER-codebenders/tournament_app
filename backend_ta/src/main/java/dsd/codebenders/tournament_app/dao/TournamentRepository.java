package dsd.codebenders.tournament_app.dao;

import java.util.List;
import java.util.Optional;

import dsd.codebenders.tournament_app.entities.Tournament;
import dsd.codebenders.tournament_app.entities.utils.TournamentStatus;
import dsd.codebenders.tournament_app.entities.utils.TournamentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    List<Tournament> findAllByType(TournamentType type);

    List<Tournament> findByTournamentScores_Team_ID(Long ID);

    Optional<Tournament> findByNameIgnoreCaseAndStatusNot(String name, TournamentStatus status);
}