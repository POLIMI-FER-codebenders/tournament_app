package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.entities.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
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

}
