package dsd.codebenders.tournament_app.integration.tournament;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.services.MatchService;
import dsd.codebenders.tournament_app.services.PlayerService;
import dsd.codebenders.tournament_app.services.TournamentService;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestPropertySource(locations = "classpath:application-integration.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClassUploadAndSelectionTest {
    @LocalServerPort
    private int port;

    @Autowired
    private PlayerService playerService;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private MatchService matchService;


    private Player teamNineLeader = new Player("playerElevenLeader", "pnl2@pnl2.pl", "testTestT1");
    private Player teamTenLeader = new Player("playerTwelveLeader", "ptl3@ptl3.pl", "testTestT1");

    private String[] teamNineInfo = {"TeamNine", "1", "OPEN"};
    private String[] teamTenInfo = {"TeamTen", "1", "OPEN"};

    // {name, teamSize, numberOfTeams, type, matchType}
    private String[] sixthTournamentInfo = {"sixthTournament", "1", "2", "KNOCKOUT", "MULTIPLAYER"};

    Long tournamentID = -1L;
    Long classID = -1L;

    @Test
    @Order(1)
    void addPlayersAndTeams(){
        playerService.addNewPlayer(teamNineLeader);
        playerService.addNewPlayer(teamTenLeader);

        // Login as teamFiveLeader
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", teamNineLeader.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // Create TeamNine
        ObjectMapper teamMapper = new ObjectMapper();
        ObjectNode team = teamMapper.createObjectNode();

        team.put("name", teamNineInfo[0]);
        team.put("maxNumberOfPlayers", Integer.parseInt(teamNineInfo[1]));
        team.put("policy", teamNineInfo[2]);

        HttpResponse<String> createTeamResponse = Unirest.post(createURLWithPort("/api/team/create"))
                .header("Content-Type", "application/json")
                .body(team.toString())
                .asString();

        assertEquals(200, createTeamResponse.getStatus());

        // Create tournament with teamsize 1
        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", sixthTournamentInfo[0]);
        tournament.put("teamSize", Integer.parseInt(sixthTournamentInfo[1]));
        tournament.put("numberOfTeams", Integer.parseInt(sixthTournamentInfo[2]));
        tournament.put("type", sixthTournamentInfo[3]);
        tournament.put("matchType", sixthTournamentInfo[4]);

        HttpResponse<String> successfulTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();

        assertEquals(200, successfulTournamentCreationResponse.getStatus());

        // join tournament
        ObjectMapper tournamentBodyMapper = new ObjectMapper();
        ObjectNode tournamentBody = tournamentBodyMapper.createObjectNode();

        tournamentID = tournamentService.getActiveTournamentByName(sixthTournamentInfo[0]).get().getID();

        tournamentBody.put("idTournament", tournamentID);

        HttpResponse<String> successfulJoinTournamentResponse = Unirest.post(createURLWithPort("/api/tournament/join"))
                .header("Content-Type", "application/json")
                .body(tournamentBody.toString())
                .asString();

        assertEquals(200, successfulJoinTournamentResponse.getStatus());

        // Login as teamTenLeader
        loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", teamTenLeader.getUsername())
                .field("password", "testTestT1")
                .asString();

        location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // Create TeamTen
        teamMapper = new ObjectMapper();
        team = teamMapper.createObjectNode();

        team.put("name", teamTenInfo[0]);
        team.put("maxNumberOfPlayers", Integer.parseInt(teamTenInfo[1]));
        team.put("policy", teamTenInfo[2]);

        createTeamResponse = Unirest.post(createURLWithPort("/api/team/create"))
                .header("Content-Type", "application/json")
                .body(team.toString())
                .asString();

        assertEquals(200, createTeamResponse.getStatus());
    }

    @Test
    @Order(2)
    void classNameExistsTest() throws JsonProcessingException {
        // Login as teamTenLeader
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", teamNineLeader.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        HttpResponse<String> classUploadResponse = Unirest.post(createURLWithPort("/api/classes/upload"))
                .multiPartContent()
                .field("file", new File("src/main/resources/game_classes/SimpleExample.java"))
                .asString();

        assertEquals(400, classUploadResponse.getStatus());
        assertEquals("A class with the same filename has been already uploaded, choose another one.", classUploadResponse.getBody());

    }

    @Test
    @Order(3)
    void classSuccesfulUploadTest() throws JsonProcessingException {

        HttpResponse<String> classUploadResponse = Unirest.post(createURLWithPort("/api/classes/upload"))
                .multiPartContent()
                .field("file", new File("src/main/resources/game_classes/TestClass.java"))
                .asString();


        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(classUploadResponse.getBody());

        classID = response.get("id").asLong();
        String filename = response.get("filename").asText();
        String authorName = response.get("author").get("name").asText();

        assertEquals(200, classUploadResponse.getStatus());
        assertEquals("TestClass.java", filename);
        assertEquals(teamNineLeader.getUsername(), authorName);
    }

    @Test
    @Order(4)
    void classSelectionTest() throws JsonProcessingException {

        ObjectMapper classSelectionMapper = new ObjectMapper();
        ObjectNode classSelection = classSelectionMapper.createObjectNode();

        classSelection.put("idTournament", tournamentID);
        classSelection.put("roundNumber", 1);
        classSelection.put("classId", classID);

        System.out.println(classSelection.toString());

        HttpResponse<String> classSelectionResponse = Unirest.post(createURLWithPort("/api/classes/post-choices"))
                .header("Content-Type", "application/json")
                .body(classSelection.toString())
                .asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(classSelectionResponse.getBody());


        System.out.println(classSelectionResponse.getBody());

        Long tournament = response.get("tournament").asLong();
        Long round = response.get("round").asLong();
        Long gameClassId = response.get("gameClass").get("id").asLong();
        String gameClassFilename = response.get("gameClass").get("filename").asText();
        String gameClassAuthor = response.get("gameClass").get("author").asText();

        assertEquals(200, classSelectionResponse.getStatus());
        assertEquals(tournamentID, tournament);
        assertEquals(1, round);
        assertEquals(classID, gameClassId);
        assertEquals("TestClass.java", gameClassFilename);
        assertEquals(teamNineLeader.getUsername(), gameClassAuthor);
    }

    @AfterAll
    public void cleanUp(){
        Unirest.shutDown();
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
