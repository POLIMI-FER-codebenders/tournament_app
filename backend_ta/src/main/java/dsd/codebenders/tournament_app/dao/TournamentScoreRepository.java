package dsd.codebenders.tournament_app.dao;

import java.util.List;
import java.util.Optional;

import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.entities.Tournament;
import dsd.codebenders.tournament_app.entities.TournamentScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface TournamentScoreRepository extends JpaRepository<TournamentScore, Long> {
    List<TournamentScore> findByTournament_ID(Long ID);

    long deleteByTeamID(Long teamID);

    Optional<TournamentScore> findByTeamAndTournament(Team team, Tournament tournament);

}
