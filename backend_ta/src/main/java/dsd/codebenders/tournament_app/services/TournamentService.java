package dsd.codebenders.tournament_app.services;

import java.util.List;
import java.util.Optional;

import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.dao.TournamentRepository;
import dsd.codebenders.tournament_app.dao.TournamentScoreRepository;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.entities.Tournament;
import dsd.codebenders.tournament_app.entities.TournamentScore;
import dsd.codebenders.tournament_app.entities.utils.TournamentStatus;
import dsd.codebenders.tournament_app.entities.utils.TournamentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentScoreRepository tournamentScoreRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public TournamentService(TeamRepository teamRepository, TournamentRepository tournamentRepository, TournamentScoreRepository tournamentScoreRepository) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentScoreRepository = tournamentScoreRepository;
        this.teamRepository = teamRepository;
    }

    public Tournament findById(Long ID) {
        return tournamentRepository.findById(ID).orElse(null);
    }

    public Tournament createTournament(Tournament tournament, Player creator) {
        tournament.setCreator(creator);
        return tournamentRepository.save(tournament);
    }

    public Tournament addTeam(Tournament tournament, Team team) {
        TournamentScore tournamentScore = new TournamentScore(tournament, team);
        tournamentScoreRepository.save(tournamentScore);
        team.setInTournament(true);
        teamRepository.save(team);
        return tryAdvance(getTournamentByID(tournament.getID()).get());
    }

    public Tournament removeTeam(Tournament tournament, Team team) {
        tournamentScoreRepository.deleteByTeamID(team.getID());
        team.setInTournament(false);
        teamRepository.save(team);
        return getTournamentByID(tournament.getID()).get();
    }

    public Tournament tryAdvance(Tournament tournament) {
        switch (tournament.getStatus()) {
            case TEAMS_JOINING: {
                if (getTournamentTeams(tournament).size() == tournament.getNumberOfTeams()) {
                    tournament.setStatus(TournamentStatus.SCHEDULING);
                    tournamentRepository.save(tournament);
                }
                break;
            }
            case SCHEDULING: {
                break;
            }
            case IN_PROGRESS: {
                break;
            }
        }
        return tournament;
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

    public Optional<Tournament> getTournamentByID(Long ID) {
        return tournamentRepository.findById(ID);
    }

    public Optional<Tournament> getActiveTournamentByName(String name) {
        return tournamentRepository.findByNameIgnoreCaseAndStatusNot(name,TournamentStatus.ENDED);
    }

    public List<Tournament> getJoinedTournaments(Player player) {
        return tournamentRepository.findByTournamentScores_Team_ID(player.getTeam().getID());
    }

    public Optional<Tournament> getActiveTournament(Player player) {
        return getJoinedTournaments(player).stream().filter(t -> t.getStatus() != TournamentStatus.ENDED).findFirst();
    }

    public List<Tournament> getTournamentsOfType(TournamentType type) {
        return tournamentRepository.findAllByType(type);
    }

    public List<Team> getTournamentTeams(Tournament tournament) {
        return tournamentScoreRepository.findByTournament_ID(tournament.getID()).stream().map(TournamentScore::getTeam).toList();
    }
}
