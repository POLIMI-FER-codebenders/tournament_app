package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.CDGameCLassRepository;
import dsd.codebenders.tournament_app.dao.GameClassRepository;
import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.dao.ServerRepository;
import dsd.codebenders.tournament_app.entities.CDGameClass;
import dsd.codebenders.tournament_app.entities.GameClass;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureMockMvc
class CDGameClassServiceTest {

    @Autowired
    private CDGameClassService cdGameClassService;

    @Autowired
    private CDGameCLassRepository cdGameCLassRepository;

    @Autowired
    private GameClassRepository gameClassRepository;

    @Autowired
    private ServerRepository serverRepository;

    @Autowired
    private PlayerRepository playerRepository;

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("p", "p", "p");
        player.setIsAdmin(false);
    }

    @Test
    void testGetCDGameClassByServer() {
        // Initialize player in the database
        playerRepository.save(player);
        // Create and save test GameClass and Server objects
        GameClass gameClass = new GameClass();
        gameClass.setFilename("gameClass.java");
        gameClass.setAuthor(player);
        gameClass.setData("public class Test {}".getBytes());
        gameClassRepository.save(gameClass);
        Server server = new Server();
        server.setAddress("https://dummyaddress.com");
        server.setActive(true);
        server.setAdminToken("dummyToken");
        serverRepository.save(server);

        // Create and save test CDGameClass object
        CDGameClass cdGameClass = new CDGameClass();
        cdGameClass.setRealClass(gameClass);
        cdGameClass.setServer(server);
        cdGameCLassRepository.save(cdGameClass);

        // Verify that the correct CDGameClass is returned by the service
        CDGameClass retrievedCDGameClass = cdGameClassService.getCDGameClassByServer(gameClass, server);
        assertThat(retrievedCDGameClass).isEqualTo(cdGameClass);
    }

    @Test
    void testAddNewCDGameClass() {
        // Initialize player in the database
        playerRepository.save(player);
        // Create and save test GameClass and Server objects
        GameClass gameClass = new GameClass();
        gameClass.setFilename("gameClass.java");
        gameClass.setAuthor(player);
        gameClass.setData("public class Test {}".getBytes());
        gameClassRepository.save(gameClass);
        Server server = new Server();
        server.setAddress("https://dummyaddress.com");
        server.setActive(true);
        server.setAdminToken("dummyToken");
        serverRepository.save(server);

        // Create and save test CDGameClass object
        CDGameClass cdGameClass = new CDGameClass();
        cdGameClass.setRealClass(gameClass);
        cdGameClass.setServer(server);
        cdGameClassService.addNewCDGameClass(cdGameClass);

        // Verify that the CDGameClass was saved successfully
        CDGameClass retrievedCDGameClass = cdGameCLassRepository.findByRealClassAndServer(gameClass, server);
        assertThat(retrievedCDGameClass).isEqualTo(cdGameClass);
    }

}
