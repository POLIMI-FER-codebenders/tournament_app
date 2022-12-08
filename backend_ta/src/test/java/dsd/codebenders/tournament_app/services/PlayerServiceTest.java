package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.entities.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class PlayerServiceTest {

    @Autowired
    PlayerService playerService;

    @Test
    void findById() {
        System.out.println(playerService.findById(1L));
    }

    @Test
    void findByUsername() {
    }

    @Test
    void checkUsernameAlreadyTaken() {
    }

    @Test
    void checkEmailAlreadyTaken() {
    }

    @Test
    void addNewPlayer() {
        playerService.addNewPlayer(new Player("mario", "mario@gmail.com", "MarioPassword"));
        assertNotNull(playerService.findByUsername("mario"));
    }

    @Test
    void getAllPlayers() {
    }

    @Test
    void getSelf() {
    }

    @Test
    void getPlayersByTeam() {
    }

    @Test
    void spoofID() {
    }
}