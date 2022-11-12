package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

    private TeamRepository teamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public Team createTeam(Team team, Player creator) {
        team.setCreator(creator);
        return teamRepository.save(team);
    }
}
