package dsd.codebenders.tournament_app.dao;

import dsd.codebenders.tournament_app.entities.Tournament;
import dsd.codebenders.tournament_app.entities.TournamentScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentScoreRepository extends JpaRepository<TournamentScore, Long> {
}
