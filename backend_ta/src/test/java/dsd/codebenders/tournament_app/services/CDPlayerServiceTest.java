package dsd.codebenders.tournament_app.services;


import dsd.codebenders.tournament_app.dao.CDPlayerRepository;
import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.dao.ServerRepository;
import dsd.codebenders.tournament_app.entities.*;
import dsd.codebenders.tournament_app.entities.utils.TeamPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureMockMvc
class CDPlayerServiceTest {

    @Autowired
    private CDPlayerService cdPlayerService;

    @Autowired
    private ServerRepository serverRepository;

    private Player player;
    private Team team;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private CDPlayerRepository cdPlayerRepository;

    @BeforeEach
    void setUp() {
        team = new Team("a", 2, null, TeamPolicy.OPEN, false, LocalDate.now());
        player = new Player("p", "p", "p");
        player.setIsAdmin(false);
    }

    @Test
    void TestGetCDPlayerByPlayerAndServer() {
        // Initialize player in the database
        playerRepository.save(player);
        // Create and save test Server object
        Server server = new Server();
        server.setAddress("https://dummyaddress.com");
        server.setActive(true);
        server.setAdminToken("dummyToken");
        serverRepository.save(server);

        // Create and save test CDPlayer class
        CDPlayer cdPlayer = new CDPlayer();
        cdPlayer.setRealPlayer(player);
        cdPlayer.setServer(server);
        cdPlayer.setUsername("p");
        cdPlayer.setUserId(1);
        cdPlayer.setToken("dummyToken");
        cdPlayerRepository.save(cdPlayer);

        // Verify that the correct CDPlayer is returned by the service
        CDPlayer retrievedCDPlayer = cdPlayerService.getCDPlayerByServer(player, server);
        assertThat(retrievedCDPlayer).isEqualTo(cdPlayer);
    }

}
