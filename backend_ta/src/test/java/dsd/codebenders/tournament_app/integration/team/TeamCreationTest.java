package dsd.codebenders.tournament_app.integration.team;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.services.PlayerService;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import org.springframework.test.context.TestPropertySource;

import java.util.List;


@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TeamCreationTest {

    @LocalServerPort
    private int port;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private TeamRepository teamRepository;

    private Player mario = new Player("mario459", "mario@mario.hr", "testTestT1");
    private String[] marioTeam = {"Mario", "2", "OPEN"};


    @BeforeAll
    public void addPlayer(){
        playerService.addNewPlayer(mario);
    }
    @Test
    @Order(1)
    void successfulTeamCreationTest() throws JSONException, JsonProcessingException {

        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", "mario459")
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() -7 );

        assertEquals("success", location);


        ObjectMapper teamMapper = new ObjectMapper();
        ObjectNode team = teamMapper.createObjectNode();

        team.put("name", marioTeam[0]);
        team.put("maxNumberOfPlayers", Integer.parseInt(marioTeam[1]));
        team.put("policy", marioTeam[2]);

        HttpResponse<String> createTeamResponse = Unirest.post(createURLWithPort("/api/team/create"))
                .header("Content-Type", "application/json")
                .body(team.toString())
                .asString();

        //ObjectMapper responseMapper = new ObjectMapper();
        //Object json = responseMapper.readValue(createTeamResponse.getBody(), Object.class);
        //String indented = responseMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        //System.out.println(indented);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(createTeamResponse.getBody());
        String teamName = response.get("name").asText();
        String teamPolicy = response.get("policy").asText();
        String teamMaxSize = response.get("maxNumberOfPlayers").asText();
        JsonNode creator = response.get("creator");

        assertEquals(marioTeam[0], teamName);
        assertEquals(marioTeam[1], teamMaxSize.toString());
        assertEquals(marioTeam[2], teamPolicy);
        assertEquals(mario.getUsername(), creator.get("username").asText());
        assertEquals(mario.getEmail(), creator.get("email").asText());
    }

    @Test
    @Order(2)
    void failedTeamCreationTestAlreadyInTeam() throws JSONException, JsonProcessingException {
        // we try to create team two times in this test so that test works both individually and with other tests

        ObjectMapper teamMapper = new ObjectMapper();
        ObjectNode team = teamMapper.createObjectNode();

        team.put("name", marioTeam[0]);
        team.put("maxNumberOfPlayers", Integer.parseInt(marioTeam[1]));
        team.put("policy", marioTeam[2]);

        HttpResponse<String> createTeamResponse = Unirest.post(createURLWithPort("/api/team/create"))
                .header("Content-Type", "application/json")
                .body(team.toString())
                .asString();


        assertEquals(400, createTeamResponse.getStatus());
        assertEquals("You are already in a team, you can't create a new one.", createTeamResponse.getBody());
    }

    @Test
    @Order(3)
    void getPersonalTeam() throws JSONException, JsonProcessingException {

        ObjectMapper teamMapper = new ObjectMapper();
        ObjectNode team = teamMapper.createObjectNode();

        team.put("name", marioTeam[0]);
        team.put("maxNumberOfPlayers", Integer.parseInt(marioTeam[1]));
        team.put("policy", marioTeam[2]);

        HttpResponse<String> getPersonalTeamResponse = Unirest.get(createURLWithPort("/api/team/get-mine"))
                .header("Content-Type", "application/json")
                .asString();


        assertEquals(200, getPersonalTeamResponse.getStatus());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(getPersonalTeamResponse.getBody());
        String teamName = response.get("name").asText();
        String teamPolicy = response.get("policy").asText();
        String teamMaxSize = response.get("maxNumberOfPlayers").asText();
        JsonNode creator = response.get("creator");

        assertEquals(marioTeam[0], teamName);
        assertEquals(marioTeam[1], teamMaxSize.toString());
        assertEquals(marioTeam[2], teamPolicy);
        assertEquals(mario.getUsername(), creator.get("username").asText());
        assertEquals(mario.getEmail(), creator.get("email").asText());

    }



    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
