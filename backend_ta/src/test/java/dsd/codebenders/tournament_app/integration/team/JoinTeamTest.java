package dsd.codebenders.tournament_app.integration.team;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.entities.utils.TeamPolicy;
import dsd.codebenders.tournament_app.services.PlayerService;
import dsd.codebenders.tournament_app.services.TeamService;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class JoinTeamTest {

    @LocalServerPort
    private int port;

    @Autowired
    private PlayerService playerService;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private TeamService teamService;
    @Autowired
    private TeamRepository teamRepository;

    private Player player1 = new Player("player1", "player1@player.pl", "testTestT1");
    private Player player2 = new Player("player2", "player2@player.pl", "testTestT1");
    private Player player3 = new Player("player3", "player3@player.pl", "testTestT1");
    private Player player4 = new Player("player4", "player4@player.pl", "testTestT1");

    private Object[] playerOneTeam = {"PlayerOneTeam", 2, TeamPolicy.OPEN};
    private Object[] playeThreeTeam = {"PlayerThreeTeam", 2, TeamPolicy.CLOSED};

    @BeforeAll
    private void addPlayersAndTeam(){
        playerService.addNewPlayer(player1);
        playerService.addNewPlayer(player2);

        Team newTeam = new Team((String) playerOneTeam[0],
                (Integer) playerOneTeam[1],
                (TeamPolicy) playerOneTeam[2]);

        teamService.createTeam(newTeam, player1);
    }

    @Test
    @Order(1)
    void unauthenticatedJoinTeamTest() throws JSONException, JsonProcessingException {

        // this test is kinda janky as i couldn't make unirest follow 302 redirect

        System.out.println("TESTING 1 : " + teamRepository.findByName("PlayerOneTeam"));
        System.out.println("PLAYERS 1 :" + playerRepository.findAll());

        ObjectMapper teamJoinBodyMapper = new ObjectMapper();
        ObjectNode teamJoinBody = teamJoinBodyMapper.createObjectNode();

        teamJoinBody.put("idTeam", teamRepository.findByName((String) playerOneTeam[0]).getID());


        HttpResponse<String> failedJoinTeamResponse = Unirest.post(createURLWithPort("/api/team/create"))
                .header("Content-Type", "application/json")
                .body(teamJoinBody.toString())
                .asString();

        String location = failedJoinTeamResponse.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 5);


        assertEquals(302, failedJoinTeamResponse.getStatus());
        assertEquals("error", location);
    }

    @Test
    @Order(2)
    void joinOpenTeamTest() throws JSONException, JsonProcessingException {



        System.out.println("TESTING 2: " + teamRepository.findByName("PlayerOneTeam"));
        System.out.println("PLAYERS 2:" + playerRepository.findAll());


        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", "player2")
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        ObjectMapper teamJoinBodyMapper = new ObjectMapper();
        ObjectNode teamJoinBody = teamJoinBodyMapper.createObjectNode();

        teamJoinBody.put("idTeam", teamRepository.findByName((String) playerOneTeam[0]).getID());

        HttpResponse<String> joinOpenTeamResponse = Unirest.post(createURLWithPort("/api/team/join"))
                .header("Content-Type", "application/json")
                .body(teamJoinBody.toString())
                .asString();

        assertEquals(200, joinOpenTeamResponse.getStatus());

        HttpResponse<String> response = Unirest.get(createURLWithPort("/api/team/get-mine"))
                .header("Content-Type", "application/json")
                .asString();

        System.out.println(response.getBody());

        /*boolean isFirst = teamRepository.findByName((String) playerOneTeam[0]).isPlayerInTeam(player1);
        boolean isSecond = teamRepository.findByName((String) playerOneTeam[0]).isPlayerInTeam(player2);
        boolean isThird = teamRepository.findByName((String) playerOneTeam[0]).isPlayerInTeam(player3);
        boolean isFourth = teamRepository.findByName((String) playerOneTeam[0]).isPlayerInTeam(player4);
        */

        /*String teamNM = (String) playerOneTeam[0];
        Team team = teamRepository.findByName(teamNM);
        System.out.println(team);*/



        /*System.out.println(isFirst);
        System.out.println(isSecond);
        System.out.println(isThird);
        System.out.println(isFourth);*/



    }

    @Test
    @Order(3)
    void testTest() throws JSONException, JsonProcessingException {
        System.out.println("TESTING 3: " + teamRepository.findByName("PlayerOneTeam"));
        System.out.println("PLAYERS 3:" + playerRepository.findAll());


    }




    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}


