package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.entities.utils.TeamPolicy;
import dsd.codebenders.tournament_app.entities.utils.TeamRole;
import dsd.codebenders.tournament_app.errors.BadRequestException;
import dsd.codebenders.tournament_app.errors.ResourceNotFoundException;
import dsd.codebenders.tournament_app.responses.TeamMemberResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository, PlayerRepository playerRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    public Team findById(Long ID){
        return teamRepository.findById(ID).orElseThrow(() -> new ResourceNotFoundException("Invalid team id"));
    }

    public Team createTeam(Team team, Player creator) {
        if(creator.getTeam() != null){
            throw new BadRequestException("You are already in a team, you can't create a new one.");
        }
        // add the creator as the first member of the team
        Set<Player> players = new HashSet<>();
        players.add(creator);
        team.setTeamMembers(players);

        team.setCreator(creator);
        team.setInTournament(false);
        team.setDateOfCreation(LocalDate.now());

        creator.setTeam(team);
        creator.setRole(TeamRole.LEADER);
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

    public List<TeamMemberResponse> getAllMembers(Long idTeam) {
        Team team = teamRepository.findById(idTeam).orElseThrow(() -> new BadRequestException("Invalid team ID"));
        return team.getTeamMembers().stream().map((x) -> new TeamMemberResponse(x.getID(), x.getUsername(), x.getRole(), 0)).collect(Collectors.toList());
    }

    public void leaveTeam(Player player) {
        Team team = player.getTeam();
        if(team == null){
            throw new BadRequestException("You are not currently in any team.");
        }
        if(team.isInTournament()){
            throw new BadRequestException("You can't leave a team currently involved in a tournament.");
        }
        if(player.getRole() == TeamRole.LEADER){
            throw new BadRequestException("The leader can't leave the team");
        }
        player.setTeam(null);
        player.setRole(null);
        playerRepository.save(player);
        team.getTeamMembers().remove(player);
        teamRepository.save(team);
    }
}
