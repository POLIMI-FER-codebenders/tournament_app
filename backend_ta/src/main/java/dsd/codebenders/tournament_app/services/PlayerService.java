package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.responses.TeamMemberResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player findById(Long ID){
        return playerRepository.findById(ID).orElse(null);
    }

    public Player findByUsername(String username) {
        return playerRepository.findByUsername(username);
    }

    public boolean checkUsernameAlreadyTaken(String username) {
        return playerRepository.findByUsername(username) != null;
    }

    public boolean checkEmailAlreadyTaken(String email) {
        return playerRepository.findByEmail(email) != null;
    }

    public void addNewPlayer(Player player) {
        playerRepository.save(player);
    }

    public boolean checkAuthentication(Player authenticatingPlayer) {
        Player DBPlayer = playerRepository.findByUsername(authenticatingPlayer.getUsername());
        if(DBPlayer == null) {
            return false;
        }
        return DBPlayer.getPassword().equals(authenticatingPlayer.getPassword());

    }

    public List<TeamMemberResponse> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map((x) -> new TeamMemberResponse(x.getID(), x.getUsername(), x.getRole(), 0))
                .collect(Collectors.toList());
    }
}
