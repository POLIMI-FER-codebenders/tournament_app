package dsd.codebenders.tournament_app.integration.tournament;

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

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreateTournamentTest {

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


    private Player playerTC = new Player("playerTournamentCreator", "ptc@ptc.pl", "testTestT1");

    // {name, teamSize, numberOfTeams, type, matchType}
    private String[] knockoutTournamentCorrectInfo = {"knockoutTournament", "1", "2", "KNOCKOUT", "MELEE"};
    private String[] leagueTournamentCorrectInfo = {"leagueTournament", "1", "2", "LEAGUE", "MELEE"};
    private String[] tournamentInfoInvalidType = {"tournamentName", "1", "2", "KNOCKOUTt", "MELEE"};
    private String[] tournamentInfoInvalidMatchType = {"tournamentName", "1", "2", "KNOCKOUT", "MELEEe"};
    private String[] tournamentInfoKnockoutPowerOfTwo = {"tournamentName", "1", "3", "KNOCKOUT", "MELEE"};
    private String[] tournamentInfoMinTeamSize   = {"tournamentName", "0", "2", "KNOCKOUT", "MELEE"};


    @BeforeAll
    void addPlayers() throws InterruptedException {
        playerService.addNewPlayer(playerTC);

    }

    @Test
    @Order(1)
    void unauthenticatedTournamentCreationTest() throws JSONException, JsonProcessingException {

        // this test is kinda janky as i couldn't make unirest follow 302 redirect

        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", knockoutTournamentCorrectInfo[0]);
        tournament.put("teamSize", Integer.parseInt(knockoutTournamentCorrectInfo[1]));
        tournament.put("numberOfTeams", Integer.parseInt(knockoutTournamentCorrectInfo[2]));
        tournament.put("type", knockoutTournamentCorrectInfo[3]);
        tournament.put("matchType", knockoutTournamentCorrectInfo[4]);

        HttpResponse<String> failedCreateTeamResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();

        String location = failedCreateTeamResponse.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 5);

        assertEquals("error", location);
    }
    @Test
    @Order(2)
    void tournamentCreationInvalidTypeTest() throws JsonProcessingException {

        // Login playerTC
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", playerTC.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // Create Tournament

        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", tournamentInfoInvalidType[0]);
        tournament.put("teamSize", Integer.parseInt(tournamentInfoInvalidType[1]));
        tournament.put("numberOfTeams", Integer.parseInt(tournamentInfoInvalidType[2]));
        tournament.put("type", tournamentInfoInvalidType[3]);
        tournament.put("matchType", tournamentInfoInvalidType[4]);

        HttpResponse<String> failedTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();

        assertEquals(400, failedTournamentCreationResponse.getStatus());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(failedTournamentCreationResponse.getBody());

        String errorMessage = response.get("error").asText();

        assertEquals("Bad Request", errorMessage);
    }

    @Test
    @Order(3)
    void tournamentCreationInvalidMatchTypeTest() throws JsonProcessingException {

        // Login playerTC
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", playerTC.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // Create Tournament

        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", tournamentInfoInvalidMatchType[0]);
        tournament.put("teamSize", Integer.parseInt(tournamentInfoInvalidMatchType[1]));
        tournament.put("numberOfTeams", Integer.parseInt(tournamentInfoInvalidMatchType[2]));
        tournament.put("type", tournamentInfoInvalidMatchType[3]);
        tournament.put("matchType", tournamentInfoInvalidMatchType[4]);

        HttpResponse<String> failedTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();

        assertEquals(400, failedTournamentCreationResponse.getStatus());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(failedTournamentCreationResponse.getBody());

        String errorMessage = response.get("error").asText();

        assertEquals("Bad Request", errorMessage);
    }

    @Test
    @Order(4)
    void tournamentCreationInvalidKnockoutPowerOfTwoTest() throws JsonProcessingException {

        // Login playerTC
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", playerTC.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // Create Tournament

        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", tournamentInfoKnockoutPowerOfTwo[0]);
        tournament.put("teamSize", Integer.parseInt(tournamentInfoKnockoutPowerOfTwo[1]));
        tournament.put("numberOfTeams", Integer.parseInt(tournamentInfoKnockoutPowerOfTwo[2]));
        tournament.put("type", tournamentInfoKnockoutPowerOfTwo[3]);
        tournament.put("matchType", tournamentInfoKnockoutPowerOfTwo[4]);

        HttpResponse<String> failedTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();

        assertEquals(400, failedTournamentCreationResponse.getStatus());
        assertEquals("The number of teams for a knockout tournament needs to be a power of 2", failedTournamentCreationResponse.getBody());
    }

    @Test
    @Order(5)
    void tournamentCreationInvalidMinTeamSizeTest() throws JsonProcessingException {

        // Login playerTC
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", playerTC.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // Create Tournament

        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", tournamentInfoMinTeamSize[0]);
        tournament.put("teamSize", Integer.parseInt(tournamentInfoMinTeamSize[1]));
        tournament.put("numberOfTeams", Integer.parseInt(tournamentInfoMinTeamSize[2]));
        tournament.put("type", tournamentInfoMinTeamSize[3]);
        tournament.put("matchType", tournamentInfoMinTeamSize[4]);

        HttpResponse<String> failedTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();


        assertEquals(400, failedTournamentCreationResponse.getStatus());
        assertEquals("The minimum team size for a knockout tournament is 1", failedTournamentCreationResponse.getBody());
    }

    @Test
    @Order(6)
    void tournamentKnockoutCreationSuccess() throws JsonProcessingException {

        // Login playerTC
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", playerTC.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // Create Tournament

        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", knockoutTournamentCorrectInfo[0]);
        tournament.put("teamSize", Integer.parseInt(knockoutTournamentCorrectInfo[1]));
        tournament.put("numberOfTeams", Integer.parseInt(knockoutTournamentCorrectInfo[2]));
        tournament.put("type", knockoutTournamentCorrectInfo[3]);
        tournament.put("matchType", knockoutTournamentCorrectInfo[4]);

        HttpResponse<String> successfulTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(successfulTournamentCreationResponse.getBody());

        String name = response.get("name").asText();
        String teamSize = response.get("teamSize").asText();
        String numberOfTeams = response.get("numberOfTeams").asText();
        String type = response.get("type").asText();
        String matchType = response.get("matchType").asText();
        String creatorUsername = response.get("creator").get("name").asText();

        assertEquals(200, successfulTournamentCreationResponse.getStatus());
        assertEquals(knockoutTournamentCorrectInfo[0], name);
        assertEquals(knockoutTournamentCorrectInfo[1], teamSize);
        assertEquals(knockoutTournamentCorrectInfo[2], numberOfTeams);
        assertEquals(knockoutTournamentCorrectInfo[3], type);
        assertEquals(knockoutTournamentCorrectInfo[4], matchType);
        assertEquals(playerTC.getUsername(), creatorUsername);
    }

    @Test
    @Order(7)
    void tournamentLeagueCreationSuccess() throws JsonProcessingException {

        // Login playerTC
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", playerTC.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // Create Tournament

        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", leagueTournamentCorrectInfo[0]);
        tournament.put("teamSize", Integer.parseInt(leagueTournamentCorrectInfo[1]));
        tournament.put("numberOfTeams", Integer.parseInt(leagueTournamentCorrectInfo[2]));
        tournament.put("type", leagueTournamentCorrectInfo[3]);
        tournament.put("matchType", leagueTournamentCorrectInfo[4]);

        HttpResponse<String> successfulTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(successfulTournamentCreationResponse.getBody());

        String name = response.get("name").asText();
        String teamSize = response.get("teamSize").asText();
        String numberOfTeams = response.get("numberOfTeams").asText();
        String type = response.get("type").asText();
        String matchType = response.get("matchType").asText();
        String creatorUsername = response.get("creator").get("name").asText();

        assertEquals(200, successfulTournamentCreationResponse.getStatus());
        assertEquals(leagueTournamentCorrectInfo[0], name);
        assertEquals(leagueTournamentCorrectInfo[1], teamSize);
        assertEquals(leagueTournamentCorrectInfo[2], numberOfTeams);
        assertEquals(leagueTournamentCorrectInfo[3], type);
        assertEquals(leagueTournamentCorrectInfo[4], matchType);
        assertEquals(playerTC.getUsername(), creatorUsername);
    }


    @AfterAll
    public void cleanUp(){
        Unirest.shutDown();
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
