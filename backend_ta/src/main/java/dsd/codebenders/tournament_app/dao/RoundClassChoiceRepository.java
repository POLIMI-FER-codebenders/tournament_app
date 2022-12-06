package dsd.codebenders.tournament_app.dao;

import dsd.codebenders.tournament_app.entities.RoundClassChoice;
import dsd.codebenders.tournament_app.entities.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoundClassChoiceRepository extends JpaRepository<RoundClassChoice, Long> {
    public RoundClassChoice findByTournamentAndRound(Tournament tournament, Integer round);
}
