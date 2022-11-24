package dsd.codebenders.tournament_app.services;

import java.util.List;
import java.util.Optional;

import dsd.codebenders.tournament_app.dao.TournamentRepository;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.entities.Tournament;
import dsd.codebenders.tournament_app.entities.TournamentScore;
import dsd.codebenders.tournament_app.entities.utils.TournamentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    @Autowired
    public TournamentService(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    public Tournament findById(Long ID) {
        return tournamentRepository.findById(ID).orElse(null);
    }

    public Tournament createTournament(Tournament tournament, Player creator) {
        tournament.setCreator(creator);
        return tournamentRepository.save(tournament);
    }

    public Tournament addTeam(Tournament tournament, Team team) {
        tournament.getTournamentScores().add(new TournamentScore(tournament, team));
        return tournamentRepository.save(tournament);
    }

    public Optional<TournamentScore> getScoreForTeam(Tournament tournament, Team team) {
        return tournament.getTournamentScores().stream().filter(ts -> ts.getTeam().equals(team)).findFirst();
    }

    public List<Team> getTeams(Tournament tournament) {
        return tournament.getTournamentScores().stream().map(TournamentScore::getTeam).toList();
    }

    public List<Tournament> getTournaments() {
        return tournamentRepository.findAll();
    }

    public List<Tournament> getTournamentsOfType(TournamentType type) {
        return tournamentRepository.findAllByType(type);
    }
}
