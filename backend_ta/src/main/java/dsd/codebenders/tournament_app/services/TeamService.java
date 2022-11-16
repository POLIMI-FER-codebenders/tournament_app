package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public Team findById(Long ID){
        return teamRepository.findById(ID).orElse(null);
    }

    public Team createTeam(Team team, Player creator) {
        team.setCreator(creator);
        return teamRepository.save(team);
    }
}
