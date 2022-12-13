package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.RoundClassChoiceRepository;
import dsd.codebenders.tournament_app.entities.RoundClassChoice;
import dsd.codebenders.tournament_app.entities.Tournament;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoundClassChoiceService {

    private final RoundClassChoiceRepository roundClassChoiceRepository;

    @Autowired
    public RoundClassChoiceService(RoundClassChoiceRepository roundClassChoiceRepository) {
        this.roundClassChoiceRepository = roundClassChoiceRepository;
    }

    public RoundClassChoice getRoundClassChoiceByTournamentAndRound(Tournament tournament, int round) {
        return roundClassChoiceRepository.findByTournamentAndRound(tournament, round);
    }

}
