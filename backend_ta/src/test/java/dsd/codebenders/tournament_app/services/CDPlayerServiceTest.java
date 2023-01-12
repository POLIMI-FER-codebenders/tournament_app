package dsd.codebenders.tournament_app.services;


import dsd.codebenders.tournament_app.dao.CDPlayerRepository;
import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.dao.ServerRepository;
import dsd.codebenders.tournament_app.dao.TeamRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureMockMvc
class CDPlayerServiceTest {

    @Autowired
    private CDPlayerService cdPlayerService;

    @Autowired
    private ServerRepository serverRepository;

    private Player player1;
    private Player player2;
    private Team team;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private CDPlayerRepository cdPlayerRepository;

    @BeforeEach
    void setUp() {
        team = new Team("a", 2, null, TeamPolicy.OPEN, false, LocalDate.now());
        player1 = new Player("player1", "player1@gmail.com", "password");
        player1.setIsAdmin(false);
        player2 = new Player("player2", "player2@gmail.com", "password");
        player2.setIsAdmin(false);
    }

    @Test
    void TestGetCDPlayerByPlayerAndServer() {
        // Initialize player in the database
        playerRepository.save(player1);
        // Create and save test Server object
        Server server = new Server();
        server.setAddress("https://dummyaddress.com");
        server.setActive(true);
        server.setAdminToken("dummyToken");
        serverRepository.save(server);

        // Create and save test CDPlayer class
        CDPlayer cdPlayer = new CDPlayer();
        cdPlayer.setRealPlayer(player1);
        cdPlayer.setServer(server);
        cdPlayer.setUsername("p");
        cdPlayer.setUserId(1);
        cdPlayer.setToken("dummyToken");
        cdPlayerRepository.save(cdPlayer);

        // Verify that the correct CDPlayer is returned by the service
        CDPlayer retrievedCDPlayer = cdPlayerService.getCDPlayerByServer(player1, server);
        assertThat(retrievedCDPlayer).isEqualTo(cdPlayer);
    }

    @Test
    void TestGetCDPlayersByTeamAndServer() {
        playerRepository.save(player1);
        team.setCreator(player1);
        teamRepository.save(team);
        // Initialize players in the database
        player1.setTeam(team);
        playerRepository.save(player1);
        player2.setTeam(team);
        playerRepository.save(player2);
        // Create and save test Server object
        Server server = new Server();
        server.setAddress("https://dummyaddress.com");
        server.setActive(true);
        server.setAdminToken("dummyToken");
        serverRepository.save(server);

        // Create and save test CDPlayer classes
        CDPlayer cdPlayer1 = new CDPlayer();
        cdPlayer1.setRealPlayer(player1);
        cdPlayer1.setServer(server);
        cdPlayer1.setUsername("player1");
        cdPlayer1.setUserId(1);
        cdPlayer1.setToken("dummyToken");
        cdPlayerRepository.save(cdPlayer1);

        CDPlayer cdPlayer2 = new CDPlayer();
        cdPlayer2.setRealPlayer(player2);
        cdPlayer2.setServer(server);
        cdPlayer2.setUsername("player2");
        cdPlayer2.setUserId(2);
        cdPlayer2.setToken("dummyToken");
        cdPlayerRepository.save(cdPlayer2);

        // Verify that the correct CDPlayers are returned by the service method
        List<CDPlayer> cdPlayerList = cdPlayerService.getCDPlayersByTeamAndServer(team, server);
        assertEquals(2, cdPlayerList.size());
        assertTrue(cdPlayerList.contains(cdPlayer1));
        assertTrue(cdPlayerList.contains(cdPlayer2));
    }

}
