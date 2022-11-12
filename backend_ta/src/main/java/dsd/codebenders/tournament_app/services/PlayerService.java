package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.entities.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public Player findByUsername(String username){
        return playerRepository.findByUsername(username);
    }

}
