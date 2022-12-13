package dsd.codebenders.tournament_app.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Transient;

import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.errors.BadRequestException;
import dsd.codebenders.tournament_app.responses.TeamMemberResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${request-debug:false}")
    private boolean debug;
    @Transient
    private Long spoofedID;
    private Logger log = LoggerFactory.getLogger(PlayerService.class);

    @Autowired
    public PlayerService(PlayerRepository playerRepository, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Player findById(Long ID) {
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
        player.setIsAdmin(false);
        playerRepository.save(player);
    }

    public List<TeamMemberResponse> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map((x) -> new TeamMemberResponse(x.getID(), x.getUsername(), x.getRole(), 0))
                .collect(Collectors.toList());
    }

    public Player getSelf() {
        if (debug && spoofedID != null) {
            return findById(spoofedID);
        } else {
            return findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        }
    }

    public List<Player> getPlayersByTeam(Team team) {
        return playerRepository.findPlayersByTeam(team);
    }

    public void spoofID(Long spoofedID) {
        if (!debug) {
            throw new BadRequestException("Tried to spoof ID without debug enabled");
        }
        if (spoofedID == 0) {
            this.spoofedID = null;
            //log.info("Unspoofed");
        } else {
            if (findById(spoofedID) == null) {
                throw new BadRequestException("Spoofed user with ID " + spoofedID + " not found");
            }
            this.spoofedID = spoofedID;
            //log.info("Spoofed to " + this.spoofedID);
        }
    }
}
