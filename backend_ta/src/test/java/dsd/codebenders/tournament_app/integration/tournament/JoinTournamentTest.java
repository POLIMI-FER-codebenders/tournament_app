package dsd.codebenders.tournament_app.integration.tournament;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dsd.codebenders.tournament_app.dao.ServerRepository;
import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.dao.TournamentRepository;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.entities.utils.TeamPolicy;
import dsd.codebenders.tournament_app.services.PlayerService;
import dsd.codebenders.tournament_app.services.TeamService;
import dsd.codebenders.tournament_app.services.TournamentService;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@TestPropertySource(locations = "classpath:application-integration.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JoinTournamentTest {

    @LocalServerPort
    private int port;

    @Autowired
    private PlayerService playerService;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TournamentService tournamentService;
    @Value("${code-defenders.default-servers.token:}")
    private String defaultServersToken;

    private Player teamOneLeader = new Player("playerOneLeader", "pol@pol.pl", "testTestT1");
    private Player teamOneMember= new Player("playerTwoMember", "ptm@ptm.pl", "testTestT1");
    private Player teamTwoLeader = new Player("playerThreeLeader", "ptl@ptl.pl", "testTestT1");
    private Player teamTwoMember = new Player("playerFourMember", "pfm@pfm.pl", "testTestT1");


    private String[] teamOneInfo = {"TeamOne", "2", "OPEN"};
    private String[] teamTwoInfo = {"TeamTwo", "2", "OPEN"};

    // {name, teamSize, numberOfTeams, type, matchType}
    private String[] firstTournamentInfo = {"firstTournament", "2", "2", "KNOCKOUT", "MULTIPLAYER"};
    private String[] secondTournamentInfo = {"secondTournament", "2", "2", "KNOCKOUT", "MULTIPLAYER"};
    private String[] thirdTournamentInfo = {"thirdTournament", "1", "2", "KNOCKOUT", "MULTIPLAYER"};

    @BeforeAll
    void addPlayersAndTeams() throws InterruptedException {
        playerService.addNewPlayer(teamOneLeader);
        playerService.addNewPlayer(teamOneMember);
        playerService.addNewPlayer(teamTwoLeader);
        playerService.addNewPlayer(teamTwoMember);

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

        // Create teamOne
        ObjectMapper teamMapper = new ObjectMapper();
        ObjectNode team = teamMapper.createObjectNode();

        team.put("name", teamOneInfo[0]);
        team.put("maxNumberOfPlayers", Integer.parseInt(teamOneInfo[1]));
        team.put("policy", teamOneInfo[2]);

        HttpResponse<String> createTeamResponse = Unirest.post(createURLWithPort("/api/team/create"))
                .header("Content-Type", "application/json")
                .body(team.toString())
                .asString();

        assertEquals(200, createTeamResponse.getStatus());

        // Login as teamOneMember
        loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", teamOneMember.getUsername())
                .field("password", "testTestT1")
                .asString();

        location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // Join teamOne
        ObjectMapper teamJoinBodyMapper = new ObjectMapper();
        ObjectNode teamJoinBody = teamJoinBodyMapper.createObjectNode();

        teamJoinBody.put("idTeam", teamRepository.findByName((String) teamOneInfo[0]).getID());

        HttpResponse<String> joinOpenTeamResponse = Unirest.post(createURLWithPort("/api/team/join"))
                .header("Content-Type", "application/json")
                .body(teamJoinBody.toString())
                .asString();

        assertEquals(200, joinOpenTeamResponse.getStatus());

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

        // Create teamTwo
        teamMapper = new ObjectMapper();
        team = teamMapper.createObjectNode();

        team.put("name", teamTwoInfo[0]);
        team.put("maxNumberOfPlayers", Integer.parseInt(teamTwoInfo[1]));
        team.put("policy", teamTwoInfo[2]);

        createTeamResponse = Unirest.post(createURLWithPort("/api/team/create"))
                .header("Content-Type", "application/json")
                .body(team.toString())
                .asString();

        assertEquals(200, createTeamResponse.getStatus());

        // Login as teamTwoMember
        loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", teamTwoMember.getUsername())
                .field("password", "testTestT1")
                .asString();

        location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);
        // Join teamTwo
        teamJoinBodyMapper = new ObjectMapper();
        teamJoinBody = teamJoinBodyMapper.createObjectNode();

        teamJoinBody.put("idTeam", teamRepository.findByName((String) teamTwoInfo[0]).getID());

        joinOpenTeamResponse = Unirest.post(createURLWithPort("/api/team/join"))
                .header("Content-Type", "application/json")
                .body(teamJoinBody.toString())
                .asString();

        assertEquals(200, joinOpenTeamResponse.getStatus());

        // As user teamTwoMember create tournament with team size 2
        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", firstTournamentInfo[0]);
        tournament.put("teamSize", Integer.parseInt(firstTournamentInfo[1]));
        tournament.put("numberOfTeams", Integer.parseInt(firstTournamentInfo[2]));
        tournament.put("type", firstTournamentInfo[3]);
        tournament.put("matchType", firstTournamentInfo[4]);

        HttpResponse<String> successfulTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();

        assertEquals(200, successfulTournamentCreationResponse.getStatus());

        // As user teamTwoMember create tournament with team size 2
        tournamentMapper = new ObjectMapper();
        tournament = tournamentMapper.createObjectNode();

        tournament.put("name", secondTournamentInfo[0]);
        tournament.put("teamSize", Integer.parseInt(secondTournamentInfo[1]));
        tournament.put("numberOfTeams", Integer.parseInt(secondTournamentInfo[2]));
        tournament.put("type", secondTournamentInfo[3]);
        tournament.put("matchType", secondTournamentInfo[4]);

        successfulTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();

        assertEquals(200, successfulTournamentCreationResponse.getStatus());

        // As user teamTwoMember create tournament with team size 1
        tournamentMapper = new ObjectMapper();
        tournament = tournamentMapper.createObjectNode();

        tournament.put("name", thirdTournamentInfo[0]);
        tournament.put("teamSize", Integer.parseInt(thirdTournamentInfo[1]));
        tournament.put("numberOfTeams", Integer.parseInt(thirdTournamentInfo[2]));
        tournament.put("type", thirdTournamentInfo[3]);
        tournament.put("matchType", thirdTournamentInfo[4]);

        successfulTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();

        assertEquals(200, successfulTournamentCreationResponse.getStatus());

        Unirest.shutDown();
    }


    @Test
    @Order(1)
    void unauthenticatedJoinTournamentTest() throws JSONException, JsonProcessingException {

        ObjectMapper tournamentBodyMapper = new ObjectMapper();
        ObjectNode tournamentBody = tournamentBodyMapper.createObjectNode();

        tournamentBody.put("idTournament", tournamentService.getActiveTournamentByName(firstTournamentInfo[0]).get().getID());

        HttpResponse<String> failedJoinTournamentResponse = Unirest.post(createURLWithPort("/api/tournament/join"))
                .header("Content-Type", "application/json")
                .body(tournamentBody.toString())
                .asString();

        String location = failedJoinTournamentResponse.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 5);

        assertEquals(302, failedJoinTournamentResponse.getStatus());
        assertEquals("error", location);
    }
    @Test
    @Order(2)
    void memberJoinTournamentTest() throws JSONException, JsonProcessingException {
        // Login as teamOneMember and try to join tournament
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", teamOneMember.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        ObjectMapper tournamentBodyMapper = new ObjectMapper();
        ObjectNode tournamentBody = tournamentBodyMapper.createObjectNode();

        tournamentBody.put("idTournament", tournamentService.getActiveTournamentByName(firstTournamentInfo[0]).get().getID());

        HttpResponse<String> failedJoinTournamentResponse = Unirest.post(createURLWithPort("/api/tournament/join"))
                .header("Content-Type", "application/json")
                .body(tournamentBody.toString())
                .asString();

        assertEquals(400, failedJoinTournamentResponse.getStatus());
        assertEquals("You are not the leader of the team", failedJoinTournamentResponse.getBody());
    }
    @Test
    @Order(3)
    void joinTournamentInvalidTeamSizeTest() throws JSONException, JsonProcessingException {
        // Login as teamOneLeader and try to join tournament with teamSize of 1
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

        ObjectMapper tournamentBodyMapper = new ObjectMapper();
        ObjectNode tournamentBody = tournamentBodyMapper.createObjectNode();

        tournamentBody.put("idTournament", tournamentService.getActiveTournamentByName(thirdTournamentInfo[0]).get().getID());

        HttpResponse<String> failedJoinTournamentResponse = Unirest.post(createURLWithPort("/api/tournament/join"))
                .header("Content-Type", "application/json")
                .body(tournamentBody.toString())
                .asString();

        assertEquals(400, failedJoinTournamentResponse.getStatus());
        assertEquals("Only teams of 1 can participate in this tournament", failedJoinTournamentResponse.getBody());

    }
    @Test
    @Order(4)
    void successfulJoinTournamentTest() throws JSONException, JsonProcessingException {
        // Login as teamOneLeader and try to join first tournament with teamSize of 2
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

        ObjectMapper tournamentBodyMapper = new ObjectMapper();
        ObjectNode tournamentBody = tournamentBodyMapper.createObjectNode();

        tournamentBody.put("idTournament", tournamentService.getActiveTournamentByName(firstTournamentInfo[0]).get().getID());

        HttpResponse<String> successfulJoinTournamentResponse = Unirest.post(createURLWithPort("/api/tournament/join"))
                .header("Content-Type", "application/json")
                .body(tournamentBody.toString())
                .asString();

        assertEquals(200, successfulJoinTournamentResponse.getStatus());


        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(successfulJoinTournamentResponse.getBody());

        String name = response.get("name").asText();
        String teamSize = response.get("teamSize").asText();
        String numberOfTeams = response.get("numberOfTeams").asText();
        String type = response.get("type").asText();
        String matchType = response.get("matchType").asText();


        assertEquals(true, teamRepository.findByName(teamOneInfo[0]).isInTournament());
        assertEquals(firstTournamentInfo[0], name);
        assertEquals(firstTournamentInfo[1], teamSize);
        assertEquals(firstTournamentInfo[2], numberOfTeams);
        assertEquals(firstTournamentInfo[3], type);
        assertEquals(firstTournamentInfo[4], matchType);


    }
    @Test
    @Order(5)
    void joinTournamentAlreadyInTournamentTest() throws JSONException, JsonProcessingException {
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

        // Join second tournament with teamSize of 2

        ObjectMapper tournamentBodyMapper = new ObjectMapper();
        ObjectNode tournamentBody = tournamentBodyMapper.createObjectNode();

        tournamentBody.put("idTournament", tournamentService.getActiveTournamentByName(secondTournamentInfo[0]).get().getID());

        HttpResponse<String> failedJoinTournamentResponse = Unirest.post(createURLWithPort("/api/tournament/join"))
                .header("Content-Type", "application/json")
                .body(tournamentBody.toString())
                .asString();


        assertEquals(400, failedJoinTournamentResponse.getStatus());
        assertEquals("The team is already participating in another tournament", failedJoinTournamentResponse.getBody());
    }
    @Test
    @Order(6)
    void secondTeamJoinTournamentTournamentStartTest(){
        assumeTrue(defaultServersToken != null && !defaultServersToken.isEmpty());
        // Login as teamTwoLeader
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", teamTwoLeader.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);
        // Join first tournament with teamSize of 2
        ObjectMapper tournamentBodyMapper = new ObjectMapper();
        ObjectNode tournamentBody = tournamentBodyMapper.createObjectNode();

        tournamentBody.put("idTournament", tournamentService.getActiveTournamentByName(firstTournamentInfo[0]).get().getID());

        HttpResponse<String> successfulJoinTournamentResponse = Unirest.post(createURLWithPort("/api/tournament/join"))
                .header("Content-Type", "application/json")
                .body(tournamentBody.toString())
                .asString();

        assertEquals(200, successfulJoinTournamentResponse.getStatus());
        System.out.println(successfulJoinTournamentResponse.getBody());

        // Check if tournament started
        assertEquals("SELECTING_CLASSES", tournamentService.getActiveTournamentByName(firstTournamentInfo[0]).get().getStatus().toString());

    }



















    @AfterAll
    public void cleanUp(){
        Unirest.shutDown();
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
