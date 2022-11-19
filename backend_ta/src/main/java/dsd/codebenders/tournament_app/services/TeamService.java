package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.entities.utils.TeamPolicy;
import dsd.codebenders.tournament_app.errors.BadRequestException;
import dsd.codebenders.tournament_app.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public Team findById(Long ID){
        return teamRepository.findById(ID).orElseThrow(() -> new ResourceNotFoundException("Invalid team id"));
    }

    public Team createTeam(Team team, Player creator) {
        team.setCreator(creator);
        team.setTeamMembers(new HashSet<Player>());
        team.setInTournament(false);
        return teamRepository.save(team);
    }

    public void joinTeam(Player player, Team team) {
        if(team.getPolicy() != TeamPolicy.OPEN){
            throw new BadRequestException("Team's policy is CLOSED, you need an invitation to join!");
        }
        if(team.isFull()){
            throw new BadRequestException("Team is full!");
        }
        if(team.isInTournament()){
            throw new BadRequestException("You can't join teams currently in a tournament!");
        }
        if(team.getTeamMembers().contains(player)){
            throw new BadRequestException("You are already part of the team!");
        }
        team.addMember(player);
        teamRepository.save(team);
    }
}
