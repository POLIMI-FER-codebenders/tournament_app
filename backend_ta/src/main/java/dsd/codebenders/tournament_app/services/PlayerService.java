package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.entities.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PlayerService(PlayerRepository playerRepository, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
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
        player.setPassword(passwordEncoder.encode(player.getPassword()));
        playerRepository.save(player);
    }

}
