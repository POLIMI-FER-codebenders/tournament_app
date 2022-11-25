package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.requests.TeamRequest;
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

    public TeamRequest createTeamRequest(Team team, String role) {
        Long [] members = new Long[team.getMembers().size()];
        for(int i = 0; i < members.length; i++) {
            members[i] = team.getMembers().get(i).getID();
        }
        return new TeamRequest(members, role);
    }
}
