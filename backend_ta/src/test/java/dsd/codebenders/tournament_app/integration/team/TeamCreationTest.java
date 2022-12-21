package dsd.codebenders.tournament_app.integration.team;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.services.PlayerService;
import kong.unirest.Config;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import org.springframework.test.context.TestPropertySource;

import java.time.Year;
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
    private Integer teamID = null;


    @BeforeAll
    public void addPlayer(){
        playerService.addNewPlayer(mario);
    }

    @Test
    @Order(1)
    void unauthenticatedTeamCreationTest() throws JSONException, JsonProcessingException {

        // this test is kinda janky as i couldn't make unirest follow 302 redirect

        ObjectMapper teamMapper = new ObjectMapper();
        ObjectNode team = teamMapper.createObjectNode();

        team.put("name", marioTeam[0]);
        team.put("maxNumberOfPlayers", Integer.parseInt(marioTeam[1]));
        team.put("policy", marioTeam[2]);

        HttpResponse<String> failedCreateTeamResponse = Unirest.post(createURLWithPort("/api/team/create"))
                .header("Content-Type", "application/json")
                .body(team.toString())
                .asString();


        String location = failedCreateTeamResponse.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 5);

        assertEquals("error", location);
    }
    @Test
    @Order(2)
    void successfulTeamCreationTest() throws JSONException, JsonProcessingException {
        // user creates the team, chooses name, size and policy for joining
        // user which creates team gets added as a member of the team with role LEADER

        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", "mario459")
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

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

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(createTeamResponse.getBody());

        String teamName = response.get("name").asText();
        String teamPolicy = response.get("policy").asText();
        String teamMaxSize = response.get("maxNumberOfPlayers").asText();

        String creatorUsername = response.get("teamMembers").get(0).get("username").asText();
        String creatorRole = response.get("teamMembers").get(0).get("role").asText();

        teamID = Integer.parseInt(response.get("id").asText());


        assertNotNull(teamID);

        assertEquals(marioTeam[0], teamName);
        assertEquals(marioTeam[1], teamMaxSize.toString());
        assertEquals(marioTeam[2], teamPolicy);

        assertEquals(mario.getUsername(), creatorUsername);
        assertEquals("LEADER", creatorRole);
    }

    @Test
    @Order(3)
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
    @Order(4)
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

    @Test
    @Order(5)
    void getTeamByID() throws JSONException, JsonProcessingException {

        HttpResponse<String> getTeamByIDResponse = Unirest.get(createURLWithPort("/api/team/get?id=" + teamID))
                .header("Content-Type", "application/json")
                .asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(getTeamByIDResponse.getBody());

        String teamName = response.get("name").asText();
        String teamPolicy = response.get("policy").asText();
        String teamMaxSize = response.get("maxNumberOfPlayers").asText();

        assertEquals(marioTeam[0], teamName);
        assertEquals(marioTeam[1], teamMaxSize.toString());
        assertEquals(marioTeam[2], teamPolicy);
    }

    @Test
    @Order(6)
    void getNonExistantTeam() throws JSONException, JsonProcessingException {

        HttpResponse<String> getTeamByIDResponse = Unirest.get(createURLWithPort("/api/team/get?id=" + 44400))
                .header("Content-Type", "application/json")
                .asString();

        assertEquals(400, getTeamByIDResponse.getStatus());
        assertEquals("Invalid team id", getTeamByIDResponse.getBody());
    }

    @AfterAll
    public void cleanUp(){
        Unirest.shutDown();
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
