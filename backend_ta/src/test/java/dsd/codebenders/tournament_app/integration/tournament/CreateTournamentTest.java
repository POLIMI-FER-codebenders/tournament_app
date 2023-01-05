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
import dsd.codebenders.tournament_app.services.MatchService;
import dsd.codebenders.tournament_app.services.PlayerService;
import dsd.codebenders.tournament_app.services.TeamService;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import java.time.Year;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(locations = "classpath:application-integration.properties")
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

    @Value("${game.tournament.league.min-teams:2}")
    private int minLeagueTeams;
    @Value("${game.tournament.league.max-teams:8}")
    private int maxLeagueTeams;
    @Value("${game.tournament.league.min-team-size:1}")
    private int minLeagueTeamSize;
    @Value("${game.tournament.league.max-team-size:10}")
    private int maxLeagueTeamSize;


    @Value("${game.tournament.knockout.min-teams:2}")
    private int minKnockoutTeams;
    @Value("${game.tournament.knockout.max-teams:16}")
    private int maxKnockoutTeams;
    @Value("${game.tournament.knockout.min-team-size:1}")
    private int minKnockoutTeamSize;
    @Value("${game.tournament.knockout.max-team-size:10}")
    private int maxKnockoutTeamSize;


    private Player playerTC = new Player("playerTournamentCreator", "ptc@ptc.pl", "testTestT1");

    // {name, teamSize, numberOfTeams, type, matchType}
    private String[] knockoutTournamentCorrectInfo = {"knockoutTournament", "1", "2", "KNOCKOUT", "MULTIPLAYER"};
    private String[] leagueTournamentCorrectInfo = {"leagueTournament", "1", "2", "LEAGUE", "MULTIPLAYER"};
    private String[] tournamentInfoInvalidType = {"tournamentName", "1", "2", "KNOCKOUTt", "MULTIPLAYER"};
    private String[] tournamentInfoInvalidMatchType = {"tournamentName", "1", "2", "KNOCKOUT", "MULTIPLAYERr"};
    private String[] tournamentInfoBlankName   = {"", "1", "2", "KNOCKOUT", "MULTIPLAYER"};
    private String[] tournamentInfoNameTooLong   = {"DVvupgpsKkHilZQgAzwfkJRytnniEdGejOKfPLXkDVMJWWzRJuKLnYSVMmjnhlOfzOJpiqLuptmwnBlRaQRghBACEuEtTxJZLtLIXRkfeEIsfEPrpHxZeEqwOWbDlIWsvlNUZqkKByvQuELWaaZdDQeeZSbBKfNNKKYAQpngevsFPPjXyeCmVMwTKVrEmcNCziUfelwsymCuisKdkHWsZwrgUXjZKjxfVGlvvuIZBQAQaerpNnYuCkVGzhZKITNN", "1", "2", "KNOCKOUT", "MULTIPLAYER"};
    private String[] tournamentInfoMatchTypeMissing = {"tournamentName", "1", "2", "KNOCKOUT"};
    private String[] tournamentInfoMatchTypeMelee = {"tournamentName", "1", "2", "KNOCKOUT", "MELEE"};

    private String[] tournamentInfoLeagueMinTeamNum = {"tournamentName", "1", "not used", "LEAGUE", "MULTIPLAYER"};
    private String[] tournamentInfoLeagueMaxTeamNum = {"tournamentName", "1", "not used", "LEAGUE", "MULTIPLAYER"};
    private String[] tournamentInfoLeagueMinTeamSize = {"tournamentName", "not used", "2", "LEAGUE", "MULTIPLAYER"};
    private String[] tournamentInfoLeagueMaxTeamSize = {"tournamentName", "not used", "2", "LEAGUE", "MULTIPLAYER"};

    private String[] tournamentInfoKnockoutPowerOfTwo = {"tournamentName", "1", "3", "KNOCKOUT", "MULTIPLAYER"};
    private String[] tournamentInfoKnockoutMinTeamNum = {"tournamentName", "1", "not used", "KNOCKOUT", "MULTIPLAYER"};
    private String[] tournamentInfoKnockoutMaxTeamNum = {"tournamentName", "1", "not used", "KNOCKOUT", "MULTIPLAYER"};
    private String[] tournamentInfoKnockoutMinTeamSize = {"tournamentName", "not used", "2", "KNOCKOUT", "MULTIPLAYER"};
    private String[] tournamentInfoKnockoutMaxTeamSize = {"tournamentName", "not used", "2", "KNOCKOUT", "MULTIPLAYER"};




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
    void tournamentInvalidTypeTest() throws JsonProcessingException {

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
    void tournamentInvalidMatchTypeTest() throws JsonProcessingException {
        // Logged in as playerTC
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
    void tournamentBlankNameTest() throws JsonProcessingException {
        // Logged in as playerTC
        // Create Tournament

        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", tournamentInfoBlankName[0]);
        tournament.put("teamSize", Integer.parseInt(tournamentInfoBlankName[1]));
        tournament.put("numberOfTeams", Integer.parseInt(tournamentInfoBlankName[2]));
        tournament.put("type", tournamentInfoBlankName[3]);
        tournament.put("matchType", tournamentInfoBlankName[4]);

        HttpResponse<String> failedTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();

        assertEquals(400, failedTournamentCreationResponse.getStatus());
        assertEquals("The tournament name cannot be empty", failedTournamentCreationResponse.getBody());
    }

    @Test
    @Order(5)
    void tournamentNameTooLongTest() throws JsonProcessingException {
        // Logged in as playerTC
        // Create Tournament

        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", tournamentInfoNameTooLong[0]);
        tournament.put("teamSize", Integer.parseInt(tournamentInfoNameTooLong[1]));
        tournament.put("numberOfTeams", Integer.parseInt(tournamentInfoNameTooLong[2]));
        tournament.put("type", tournamentInfoNameTooLong[3]);
        tournament.put("matchType", tournamentInfoNameTooLong[4]);

        HttpResponse<String> failedTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();

        assertEquals(400, failedTournamentCreationResponse.getStatus());
        assertEquals("The maximum length of the tournament name is 255 characters", failedTournamentCreationResponse.getBody());
    }

    @Test
    @Order(6)
    void tournamentMatchTypeMissing() throws JsonProcessingException {
        // Logged in as playerTC
        // Create Tournament

        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", tournamentInfoMatchTypeMissing[0]);
        tournament.put("teamSize", Integer.parseInt(tournamentInfoMatchTypeMissing[1]));
        tournament.put("numberOfTeams", Integer.parseInt(tournamentInfoMatchTypeMissing[2]));
        tournament.put("type", tournamentInfoMatchTypeMissing[3]);

        HttpResponse<String> failedTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();

        assertEquals(400, failedTournamentCreationResponse.getStatus());
        assertEquals("Missing match type", failedTournamentCreationResponse.getBody());
    }

    @Test
    @Order(7)
    void tournamentMatchTypeMelee() throws JsonProcessingException {
        // Logged in as playerTC
        // Create Tournament

        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", tournamentInfoMatchTypeMelee[0]);
        tournament.put("teamSize", Integer.parseInt(tournamentInfoMatchTypeMelee[1]));
        tournament.put("numberOfTeams", Integer.parseInt(tournamentInfoMatchTypeMelee[2]));
        tournament.put("type", tournamentInfoMatchTypeMelee[3]);
        tournament.put("matchType", tournamentInfoMatchTypeMelee[4]);

        HttpResponse<String> failedTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();

        assertEquals(400, failedTournamentCreationResponse.getStatus());
        assertEquals("Melee match type is currently not supported", failedTournamentCreationResponse.getBody());
    }

    @Test
    @Order(8)
    void leagueTournamentMinNumberOfTeams() throws JsonProcessingException {
        // Logged in as playerTC
        // Create Tournament

        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", tournamentInfoLeagueMinTeamNum[0]);
        tournament.put("teamSize", Integer.parseInt(tournamentInfoLeagueMinTeamNum[1]));
        tournament.put("numberOfTeams", minLeagueTeams - 1);
        tournament.put("type", tournamentInfoLeagueMinTeamNum[3]);
        tournament.put("matchType", tournamentInfoLeagueMinTeamNum[4]);

        HttpResponse<String> failedTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();

        assertEquals(400, failedTournamentCreationResponse.getStatus());
        assertEquals("The minimum number of teams for a league tournament is " + minLeagueTeams, failedTournamentCreationResponse.getBody());
    }

    @Test
    @Order(9)
    void leagueTournamentMaxNumberOfTeams() throws JsonProcessingException {
        // Logged in as playerTC
        // Create Tournament

        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", tournamentInfoLeagueMaxTeamNum[0]);
        tournament.put("teamSize", Integer.parseInt(tournamentInfoLeagueMaxTeamNum[1]));
        tournament.put("numberOfTeams", maxLeagueTeams + 1);
        tournament.put("type", tournamentInfoLeagueMaxTeamNum[3]);
        tournament.put("matchType", tournamentInfoLeagueMaxTeamNum[4]);

        HttpResponse<String> failedTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();

        assertEquals(400, failedTournamentCreationResponse.getStatus());
        assertEquals("The maximum number of teams for a league tournament is " + maxLeagueTeams, failedTournamentCreationResponse.getBody());
    }

    @Test
    @Order(10)
    void leagueTournamentMinTeamSize() throws JsonProcessingException {
        // Logged in as playerTC
        // Create Tournament

        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", tournamentInfoLeagueMaxTeamSize[0]);
        tournament.put("teamSize", minLeagueTeamSize - 1);
        tournament.put("numberOfTeams", Integer.parseInt(tournamentInfoLeagueMaxTeamSize[2]));
        tournament.put("type", tournamentInfoLeagueMaxTeamSize[3]);
        tournament.put("matchType", tournamentInfoLeagueMaxTeamSize[4]);

        HttpResponse<String> failedTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();

        assertEquals(400, failedTournamentCreationResponse.getStatus());
        assertEquals("The minimum team size for a league tournament is " + minLeagueTeamSize, failedTournamentCreationResponse.getBody());
    }
    @Test
    @Order(11)
    void leagueTournamentMaxTeamSize() throws JsonProcessingException {
        // Logged in as playerTC
        // Create Tournament

        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", tournamentInfoLeagueMinTeamSize[0]);
        tournament.put("teamSize", maxLeagueTeamSize + 1);
        tournament.put("numberOfTeams", Integer.parseInt(tournamentInfoLeagueMinTeamSize[2]));
        tournament.put("type", tournamentInfoLeagueMinTeamSize[3]);
        tournament.put("matchType", tournamentInfoLeagueMinTeamSize[4]);

        HttpResponse<String> failedTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();

        assertEquals(400, failedTournamentCreationResponse.getStatus());
        assertEquals("The maximum team size for a league tournament is " + maxLeagueTeamSize, failedTournamentCreationResponse.getBody());
    }

    @Test
    @Order(12)
    void knockoutTournamentInvalidPowerOfTwo() throws JsonProcessingException {
        // Logged in as playerTC
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
    @Order(13)
    void knockoutTournamentMinNumberOfTeams() throws JsonProcessingException {
        // Logged in as playerTC
        // Create Tournament

        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", tournamentInfoKnockoutMinTeamNum[0]);
        tournament.put("teamSize", Integer.parseInt(tournamentInfoKnockoutMinTeamNum[1]));
        tournament.put("numberOfTeams", minKnockoutTeams - 1);
        tournament.put("type", tournamentInfoKnockoutMinTeamNum[3]);
        tournament.put("matchType", tournamentInfoKnockoutMinTeamNum[4]);

        HttpResponse<String> failedTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();


        assertEquals(400, failedTournamentCreationResponse.getStatus());
        assertEquals("The minimum number of teams for a knockout tournament is " + minKnockoutTeams, failedTournamentCreationResponse.getBody());
    }

    @Test
    @Order(14)
    void knockoutTournamentMaxNumberOfTeams() throws JsonProcessingException {
        // Logged in as playerTC
        // Create Tournament

        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", tournamentInfoKnockoutMaxTeamNum[0]);
        tournament.put("teamSize", Integer.parseInt(tournamentInfoKnockoutMaxTeamNum[1]));
        tournament.put("numberOfTeams", Math.pow(2, maxKnockoutTeams));
        tournament.put("type", tournamentInfoKnockoutMaxTeamNum[3]);
        tournament.put("matchType", tournamentInfoKnockoutMaxTeamNum[4]);

        HttpResponse<String> failedTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();


        assertEquals(400, failedTournamentCreationResponse.getStatus());
        assertEquals("The maximum team size for a knockout tournament is " + maxKnockoutTeams, failedTournamentCreationResponse.getBody());
    }

    @Test
    @Order(15)
    void knockoutTournamentMinTeamSize() throws JsonProcessingException {
        // Logged in as playerTC
        // Create Tournament

        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", tournamentInfoKnockoutMinTeamSize[0]);
        tournament.put("teamSize", minKnockoutTeamSize - 1);
        tournament.put("numberOfTeams", Integer.parseInt(tournamentInfoKnockoutMinTeamSize[2]));
        tournament.put("type", tournamentInfoKnockoutMinTeamSize[3]);
        tournament.put("matchType", tournamentInfoKnockoutMinTeamSize[4]);

        HttpResponse<String> failedTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();


        assertEquals(400, failedTournamentCreationResponse.getStatus());
        assertEquals("The minimum team size for a knockout tournament is 1", failedTournamentCreationResponse.getBody());
    }

    @Test
    @Order(16)
    void knockoutTournamentMaxTeamSize() throws JsonProcessingException {
        // Logged in as playerTC
        // Create Tournament

        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", tournamentInfoKnockoutMaxTeamSize[0]);
        tournament.put("teamSize", maxKnockoutTeamSize + 1);
        tournament.put("numberOfTeams", Integer.parseInt(tournamentInfoKnockoutMaxTeamSize[2]));
        tournament.put("type", tournamentInfoKnockoutMaxTeamSize[3]);
        tournament.put("matchType", tournamentInfoKnockoutMaxTeamSize[4]);

        HttpResponse<String> failedTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();


        assertEquals(400, failedTournamentCreationResponse.getStatus());
        assertEquals("The maximum team size for a knockout tournament is " + maxKnockoutTeamSize, failedTournamentCreationResponse.getBody());
    }

    @Test
    @Order(17)
    void tournamentKnockoutCreationSuccess() throws JsonProcessingException {
        // Logged in as playerTC
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
    @Order(18)
    void tournamentLeagueCreationSuccess() throws JsonProcessingException {
        // Logged in as playerTC
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
