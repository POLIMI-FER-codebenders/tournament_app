package dsd.codebenders.tournament_app.services;

import java.time.LocalDate;

import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.entities.utils.TeamPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class PlayerServiceTest {

    @Autowired
    PlayerService playerService;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    TeamRepository teamRepository;
    private Player player;

    @Test
    void findById() {
        assertNull(playerService.findById(1000L));
        assertNotNull(playerService.findById(playerRepository.save(player).getID()));
    }

    @Test
    void findByUsername() {
        assertNull(playerService.findByUsername("a"));
        playerRepository.save(player);
        assertNotNull(playerService.findByUsername("a"));
    }

    @Test
    void checkUsernameAlreadyTaken() {
        assertFalse(playerService.checkUsernameAlreadyTaken("a"));
        playerRepository.save(player);
        assertTrue(playerService.checkUsernameAlreadyTaken("a"));
    }

    @Test
    void checkEmailAlreadyTaken() {
        assertFalse(playerService.checkEmailAlreadyTaken("b"));
        playerRepository.save(player);
        assertTrue(playerService.checkEmailAlreadyTaken("b"));
    }

    @Test
    void addNewPlayer() {
        player = new Player("mario", "mario@gmail.com", "MarioPassword");
        playerService.addNewPlayer(player);
        assertNotNull(playerService.findByUsername("mario"));
    }

    @Test
    void getAllPlayers() {
        int oldSize = playerService.getAllPlayers().size();
        playerRepository.save(player);
        assertEquals(playerService.getAllPlayers().size(), oldSize + 1);
    }

    @Test
    void getPlayersByTeam() {
        playerRepository.save(player);
        Team team = new Team("t", 1, player, TeamPolicy.CLOSED, false, LocalDate.now());
        teamRepository.save(team);
        assertTrue(playerService.getPlayersByTeam(team).isEmpty());
        player.setTeam(team);
        playerRepository.save(player);
        assertEquals(playerService.getPlayersByTeam(team).size(), 1);
    }

    @BeforeEach
    void setUp() {
        player = new Player("a", "b", "c");
        player.setIsAdmin(false);
    }
}