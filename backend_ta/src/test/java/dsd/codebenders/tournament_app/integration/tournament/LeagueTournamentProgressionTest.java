package dsd.codebenders.tournament_app.integration.tournament;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.entities.Player;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestPropertySource(locations = "classpath:application-integration.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LeagueTournamentProgressionTest {


    @LocalServerPort
    private int port;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private TournamentService tournamentService;


    private Player teamOneLeader = new Player("leagueT1Leader", "ltl1@ltl1.pl", "testTestT1");
    private Player teamTwoLeader = new Player("leagueT2Leader", "ltl2@ltl2.pl", "testTestT1");
    private Player teamThreeLeader = new Player("leagueT3Leader", "ltl3@ltl3.pl", "testTestT1");
    private Player teamFourLeader = new Player("leagueT4Leader", "ltl4@ltl4.pl", "testTestT1");


    private String[] leagueTeamOne = {"leagueTeamOne", "1", "OPEN"};
    private String[] leagueTeamTwo = {"leagueTeamTwo", "1", "OPEN"};
    private String[] leagueTeamThree = {"leagueTeamThree", "1", "OPEN"};
    private String[] leagueTeamFour = {"leagueTeamFour", "1", "OPEN"};


    // {name, teamSize, numberOfTeams, type, matchType}
    private String[] leagueTournamentInfo = {"leagueTournamentOne", "1", "4", "LEAGUE", "MULTIPLAYER"};

    Long tournamentID = -1L;

    @Test
    @Order(1)
    void addPlayersAndTeams() {
        playerService.addNewPlayer(teamOneLeader);
        playerService.addNewPlayer(teamTwoLeader);
        playerService.addNewPlayer(teamThreeLeader);
        playerService.addNewPlayer(teamFourLeader);


        // Login as teamOneLeader
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", teamOneLeader.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // Create TeamFive
        ObjectMapper teamMapper = new ObjectMapper();
        ObjectNode team = teamMapper.createObjectNode();

        team.put("name", leagueTeamOne[0]);
        team.put("maxNumberOfPlayers", Integer.parseInt(leagueTeamOne[1]));
        team.put("policy", leagueTeamOne[2]);

        HttpResponse<String> createTeamResponse = Unirest.post(createURLWithPort("/api/team/create"))
                .header("Content-Type", "application/json")
                .body(team.toString())
                .asString();

        assertEquals(200, createTeamResponse.getStatus());

        // As user teamOneLeader create LEAGUE tournament with team size 1
        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", leagueTournamentInfo[0]);
        tournament.put("teamSize", Integer.parseInt(leagueTournamentInfo[1]));
        tournament.put("numberOfTeams", Integer.parseInt(leagueTournamentInfo[2]));
        tournament.put("type", leagueTournamentInfo[3]);
        tournament.put("matchType", leagueTournamentInfo[4]);

        HttpResponse<String> successfulTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();

        assertEquals(200, successfulTournamentCreationResponse.getStatus());


        // As teamOneLeader join tournament
        ObjectMapper tournamentBodyMapper = new ObjectMapper();
        ObjectNode tournamentBody = tournamentBodyMapper.createObjectNode();

        tournamentID = tournamentService.getActiveTournamentByName(leagueTournamentInfo[0]).get().getID();

        tournamentBody.put("idTournament", tournamentID);

        HttpResponse<String> successfulJoinTournamentResponse = Unirest.post(createURLWithPort("/api/tournament/join"))
                .header("Content-Type", "application/json")
                .body(tournamentBody.toString())
                .asString();

        assertEquals(200, successfulJoinTournamentResponse.getStatus());


        // Login as teamTwoLeader
        loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", teamTwoLeader.getUsername())
                .field("password", "testTestT1")
                .asString();

        location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // Create teamSixInfo
        teamMapper = new ObjectMapper();
        team = teamMapper.createObjectNode();

        team.put("name", leagueTeamTwo[0]);
        team.put("maxNumberOfPlayers", Integer.parseInt(leagueTeamTwo[1]));
        team.put("policy", leagueTeamTwo[2]);

        createTeamResponse = Unirest.post(createURLWithPort("/api/team/create"))
                .header("Content-Type", "application/json")
                .body(team.toString())
                .asString();

        assertEquals(200, createTeamResponse.getStatus());

        // As teamSixLeader join tournament
        tournamentBodyMapper = new ObjectMapper();
        tournamentBody = tournamentBodyMapper.createObjectNode();

        tournamentBody.put("idTournament", tournamentID);

        successfulJoinTournamentResponse = Unirest.post(createURLWithPort("/api/tournament/join"))
                .header("Content-Type", "application/json")
                .body(tournamentBody.toString())
                .asString();

        assertEquals(200, successfulJoinTournamentResponse.getStatus());



        // Login as teamThreeLeader
        loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", teamThreeLeader.getUsername())
                .field("password", "testTestT1")
                .asString();

        location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // Create teamSeven
        teamMapper = new ObjectMapper();
        team = teamMapper.createObjectNode();

        team.put("name", leagueTeamThree[0]);
        team.put("maxNumberOfPlayers", Integer.parseInt(leagueTeamThree[1]));
        team.put("policy", leagueTeamThree[2]);

        createTeamResponse = Unirest.post(createURLWithPort("/api/team/create"))
                .header("Content-Type", "application/json")
                .body(team.toString())
                .asString();

        assertEquals(200, createTeamResponse.getStatus());

        // As teamSevenLeader join tournament
        tournamentBodyMapper = new ObjectMapper();
        tournamentBody = tournamentBodyMapper.createObjectNode();

        tournamentBody.put("idTournament", tournamentID);

        successfulJoinTournamentResponse = Unirest.post(createURLWithPort("/api/tournament/join"))
                .header("Content-Type", "application/json")
                .body(tournamentBody.toString())
                .asString();

        assertEquals(200, successfulJoinTournamentResponse.getStatus());


        // Login as teamFourLeader
        loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", teamFourLeader.getUsername())
                .field("password", "testTestT1")
                .asString();

        location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // Create teamSeven
        teamMapper = new ObjectMapper();
        team = teamMapper.createObjectNode();

        team.put("name", leagueTeamFour[0]);
        team.put("maxNumberOfPlayers", Integer.parseInt(leagueTeamFour[1]));
        team.put("policy", leagueTeamFour[2]);

        createTeamResponse = Unirest.post(createURLWithPort("/api/team/create"))
                .header("Content-Type", "application/json")
                .body(team.toString())
                .asString();

        assertEquals(200, createTeamResponse.getStatus());

        // As teamSevenLeader join tournament
        tournamentBodyMapper = new ObjectMapper();
        tournamentBody = tournamentBodyMapper.createObjectNode();

        tournamentBody.put("idTournament", tournamentID);

        successfulJoinTournamentResponse = Unirest.post(createURLWithPort("/api/tournament/join"))
                .header("Content-Type", "application/json")
                .body(tournamentBody.toString())
                .asString();

        assertEquals(200, successfulJoinTournamentResponse.getStatus());


    }

    @Test
    @Order(2)
    void checkTournamentProgression() throws InterruptedException, JsonProcessingException {
        // Login as teamFiveLeader
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", teamOneLeader.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // get tournament
        HttpResponse<String> getTournamentResponse = Unirest.get(createURLWithPort("/api/tournament/personal"))
                .header("Content-Type", "application/json")
                .asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode tournamentResponse = mapper.readTree(getTournamentResponse.getBody());

        String tournamentStatus = tournamentResponse.get(0).get("status").asText();

        assertEquals("SELECTING_CLASSES", tournamentStatus);

        while (tournamentStatus.equals("SELECTING_CLASSES")){
            getTournamentResponse = Unirest.get(createURLWithPort("/api/tournament/personal"))
                    .header("Content-Type", "application/json")
                    .asString();
            mapper = new ObjectMapper();
            tournamentResponse = mapper.readTree(getTournamentResponse.getBody());
            tournamentStatus = tournamentResponse.get(0).get("status").asText();

        }

        assertEquals("IN_PROGRESS", tournamentStatus);


        while (tournamentStatus.equals("IN_PROGRESS")){
            getTournamentResponse = Unirest.get(createURLWithPort("/api/tournament/personal"))
                    .header("Content-Type", "application/json")
                    .asString();
            mapper = new ObjectMapper();
            tournamentResponse = mapper.readTree(getTournamentResponse.getBody());
            tournamentStatus = tournamentResponse.get(0).get("status").asText();

        }

        assertEquals("ENDED", tournamentStatus);
    }

    @Test
    @Order(2)
    void checkExistsWinner() throws JsonProcessingException {
        // Logged in as teamFiveLeader
        // get tournament
        HttpResponse<String> getTournamentResponse = Unirest.get(createURLWithPort("/api/tournament/personal"))
                .header("Content-Type", "application/json")
                .asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode tournamentResponse = mapper.readTree(getTournamentResponse.getBody());

        assertEquals(false, tournamentResponse.get(0).get("winningTeam").isNull());
    }

    @AfterAll
    public void cleanUp(){
        Unirest.shutDown();
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
