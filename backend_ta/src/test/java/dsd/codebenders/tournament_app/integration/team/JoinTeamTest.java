package dsd.codebenders.tournament_app.integration.team;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dsd.codebenders.tournament_app.dao.InvitationRepository;
import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.entities.utils.InvitationStatus;
import dsd.codebenders.tournament_app.entities.utils.TeamPolicy;
import dsd.codebenders.tournament_app.services.PlayerService;
import dsd.codebenders.tournament_app.services.TeamService;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.context.transaction.BeforeTransaction;

import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestPropertySource(locations = "classpath:application-integration.properties")
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
    @Autowired
    private InvitationRepository invitationRepository;

    private Player player1 = new Player("player1", "player1@player.pl", "testTestT1");
    private Player player2 = new Player("player2", "player2@player.pl", "testTestT1");
    private Player player3 = new Player("player3", "player3@player.pl", "testTestT1");
    private Player player4 = new Player("player4", "player4@player.pl", "testTestT1");
    private Player player5 = new Player("player5", "player5@player.pl", "testTestT1");

    private Object[] playerOneTeam = {"PlayerOneTeam", 2, "OPEN"};
    private Object[] playerThreeTeam = {"PlayerThreeTeam", 2, "CLOSED"};


    @BeforeAll
    void addPlayersAndCreateTeam() throws InterruptedException {
        playerService.addNewPlayer(player1);
        playerService.addNewPlayer(player2);
        playerService.addNewPlayer(player3);
        playerService.addNewPlayer(player4);
        playerService.addNewPlayer(player5);

        HttpResponse<String> loginPlayerOneSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", "player1")
                .field("password", "testTestT1")
                .asString();

        String location = loginPlayerOneSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() -7 );

        assertEquals("success", location);

        ObjectMapper teamOneMapper = new ObjectMapper();
        ObjectNode teamOne = teamOneMapper.createObjectNode();

        teamOne.put("name", (String) playerOneTeam[0]);
        teamOne.put("maxNumberOfPlayers", (Integer) playerOneTeam[1]);
        teamOne.put("policy", (String) playerOneTeam[2]);

        HttpResponse<String> createTeamOneResponse = Unirest.post(createURLWithPort("/api/team/create"))
                .header("Content-Type", "application/json")
                .body(teamOne.toString())
                .asString();

        assertEquals(200, createTeamOneResponse.getStatus());

        HttpResponse<String> logoutSuccess = Unirest.get(createURLWithPort("/authentication/logout")).asString();

        assertEquals(200, logoutSuccess.getStatus());

        HttpResponse<String> loginPlayerThreeSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", "player3")
                .field("password", "testTestT1")
                .asString();

        location = loginPlayerOneSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() -7 );

        assertEquals("success", location);

        ObjectMapper teamThreeMapper = new ObjectMapper();
        ObjectNode teamThree = teamThreeMapper.createObjectNode();

        teamThree.put("name", (String) playerThreeTeam[0]);
        teamThree.put("maxNumberOfPlayers", (Integer) playerThreeTeam[1]);
        teamThree.put("policy", (String) playerThreeTeam[2]);

        HttpResponse<String> createTeamThreeResponse = Unirest.post(createURLWithPort("/api/team/create"))
                .header("Content-Type", "application/json")
                .body(teamThree.toString())
                .asString();

        assertEquals(200, createTeamThreeResponse.getStatus());

        logoutSuccess = Unirest.get(createURLWithPort("/authentication/logout")).asString();

        assertEquals(200, logoutSuccess.getStatus());
    }

    @Test
    @Order(1)
    void unauthenticatedJoinTeamTest() throws JSONException, JsonProcessingException {

        // this test is kinda janky as i couldn't make unirest follow 302 redirect

        ObjectMapper teamJoinBodyMapper = new ObjectMapper();
        ObjectNode teamJoinBody = teamJoinBodyMapper.createObjectNode();

        teamJoinBody.put("idTeam", teamRepository.findByName((String) playerOneTeam[0]).getID());


        HttpResponse<String> failedJoinTeamResponse = Unirest.post(createURLWithPort("/api/team/join"))
                .header("Content-Type", "application/json")
                .body(teamJoinBody.toString())
                .asString();

        String location = failedJoinTeamResponse.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 5);

        assertEquals("error", location);
    }

    @Test
    @Order(2)
    void joinOpenTeamTest() throws JSONException, JsonProcessingException {

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

        HttpResponse<String> getMyTeam = Unirest.get(createURLWithPort("/api/team/get-mine"))
                .header("Content-Type", "application/json")
                .asString();


        ObjectMapper mapper = new ObjectMapper();
        JsonNode myTeamJson = mapper.readTree(getMyTeam.getBody());

        String teamName = myTeamJson.get("name").asText();
        String teamPolicy = myTeamJson.get("policy").asText();
        Integer teamMaxSize = myTeamJson.get("maxNumberOfPlayers").asInt();

        String playerUsername = myTeamJson.get("teamMembers").get(1).get("username").asText();
        String playerRole = myTeamJson.get("teamMembers").get(1).get("role").asText();


        assertEquals(playerOneTeam[0], teamName);
        assertEquals(playerOneTeam[1], teamMaxSize);
        assertEquals(playerOneTeam[2], teamPolicy);

        assertEquals(player2.getUsername(), playerUsername);
        assertEquals("MEMBER", playerRole);
    }
    @Test
    @Order(3)
    void joinClosedTeamWithoutInvitationTest() throws JSONException, JsonProcessingException {

        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", "player4")
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        ObjectMapper teamJoinBodyMapper = new ObjectMapper();
        ObjectNode teamJoinBody = teamJoinBodyMapper.createObjectNode();

        teamJoinBody.put("idTeam", teamRepository.findByName((String) playerThreeTeam[0]).getID());

        HttpResponse<String> joinClosedTeamResponse = Unirest.post(createURLWithPort("/api/team/join"))
                .header("Content-Type", "application/json")
                .body(teamJoinBody.toString())
                .asString();

        assertEquals(400, joinClosedTeamResponse.getStatus());
        assertEquals("Team's policy is CLOSED, you need an invitation to join!", joinClosedTeamResponse.getBody());
    }

    @Test
    @Order(4)
    void invitePlayerToTeamTest() throws JSONException, JsonProcessingException {

        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", "player3")
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);



        ObjectMapper inviteBodyMapper = new ObjectMapper();
        ObjectNode inviteBody = inviteBodyMapper.createObjectNode();

        inviteBody.put("idTeam", teamRepository.findByName((String) playerThreeTeam[0]).getID());
        inviteBody.put("idInvitedPlayer", playerRepository.findByUsername(player4.getUsername()).getID());


        HttpResponse<String> invitePlayerResonse = Unirest.post(createURLWithPort("/api/invitation/create"))
                .header("Content-Type", "application/json")
                .body(inviteBody.toString())
                .asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode invitationJson = mapper.readTree(invitePlayerResonse.getBody());

        String invitedPlayerUsername = invitationJson.get("invitedPlayer").get("username").asText();

        assertEquals(200, invitePlayerResonse.getStatus());
        assertEquals(player4.getUsername(), invitedPlayerUsername);
    }

    @Test
    @Order(5)
    void getPendingInvitationsTest() throws JSONException, JsonProcessingException {

        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", "player4")
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);


        HttpResponse<String> myPendingInvitationsResonse = Unirest.get(createURLWithPort("/api/invitation/pending"))
                .header("Content-Type", "application/json")
                .asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode invitationJson = mapper.readTree(myPendingInvitationsResonse.getBody());

        String invitedPlayerUsername = invitationJson.get(0).get("invitedPlayer").get("username").asText();

        assertEquals(200, myPendingInvitationsResonse.getStatus());
        assertEquals(player4.getUsername(), invitedPlayerUsername);
    }
    @Test
    @Order(6)
    void acceptOtherPlayersInvitationTest() throws JSONException, JsonProcessingException {

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

        String invitationID = invitationRepository.findByInvitedPlayerAndStatusEquals(player4, InvitationStatus.PENDING).get(0).getID().toString();

        ObjectMapper acceptInviteBodyMapper = new ObjectMapper();
        ObjectNode acceptInviteBody = acceptInviteBodyMapper.createObjectNode();

        acceptInviteBody.put("idInvitation", invitationID);

        HttpResponse<String> acceptInvitationResponse = Unirest.post(createURLWithPort("/api/invitation/accept"))
                .header("Content-Type", "application/json")
                .body(acceptInviteBody.toString())
                .asString();


        assertEquals(400, acceptInvitationResponse.getStatus());
        assertEquals("You can't accept others' invitations.", acceptInvitationResponse.getBody());
    }

    @Test
    @Order(7)
    void acceptPendingInvitationTest() throws JSONException, JsonProcessingException {

        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", "player4")
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);


        HttpResponse<String> myPendingInvitationsResonse = Unirest.get(createURLWithPort("/api/invitation/pending"))
                .header("Content-Type", "application/json")
                .asString();

        assertEquals(200, myPendingInvitationsResonse.getStatus());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode invitationJson = mapper.readTree(myPendingInvitationsResonse.getBody());

        String invitationID = invitationJson.get(0).get("id").asText();

        String invitationTeamName = invitationJson.get(0).get("team").get("name").asText();
        Long invitationTeamID = invitationJson.get(0).get("team").get("id").asLong();

        assertEquals(playerThreeTeam[0], invitationTeamName);
        assertEquals(teamRepository.findByName((String) playerThreeTeam[0]).getID(), invitationTeamID);


        ObjectMapper acceptInviteBodyMapper = new ObjectMapper();
        ObjectNode acceptInviteBody = acceptInviteBodyMapper.createObjectNode();

        acceptInviteBody.put("idInvitation", invitationID);

        HttpResponse<String> acceptInvitationResponse = Unirest.post(createURLWithPort("/api/invitation/accept"))
                .header("Content-Type", "application/json")
                .body(acceptInviteBody.toString())
                .asString();

        assertEquals(200, acceptInvitationResponse.getStatus());


        HttpResponse<String> getMyTeam = Unirest.get(createURLWithPort("/api/team/get-mine"))
                .header("Content-Type", "application/json")
                .asString();

        ObjectMapper myTeamMapper = new ObjectMapper();
        JsonNode myTeamJson = myTeamMapper.readTree(getMyTeam.getBody());

        String joinedTeamName = myTeamJson.get("name").asText();
        Long joinedTeamID = myTeamJson.get("id").asLong()   ;

        assertEquals(invitationTeamName, joinedTeamName);
        assertEquals(invitationTeamID, joinedTeamID);
    }
    @Test
    @Order(8)
    void joinFullTeamTest() throws JSONException, JsonProcessingException {

        //Login as player3
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", "player3")
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // Create invitation for player 5
        ObjectMapper inviteBodyMapper = new ObjectMapper();
        ObjectNode inviteBody = inviteBodyMapper.createObjectNode();

        inviteBody.put("idTeam", teamRepository.findByName((String) playerThreeTeam[0]).getID());
        inviteBody.put("idInvitedPlayer", playerRepository.findByUsername(player5.getUsername()).getID());


        HttpResponse<String> invitePlayerResonse = Unirest.post(createURLWithPort("/api/invitation/create"))
                .header("Content-Type", "application/json")
                .body(inviteBody.toString())
                .asString();


        assertEquals(200, invitePlayerResonse.getStatus());

        //Login as player5
        loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", "player5")
                .field("password", "testTestT1")
                .asString();

        location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        //Try to join teamOne as player5
        ObjectMapper teamJoinBodyMapper = new ObjectMapper();
        ObjectNode teamJoinBody = teamJoinBodyMapper.createObjectNode();

        teamJoinBody.put("idTeam", teamRepository.findByName((String) playerOneTeam[0]).getID());

        HttpResponse<String> joinOpenTeamResponse = Unirest.post(createURLWithPort("/api/team/join"))
                .header("Content-Type", "application/json")
                .body(teamJoinBody.toString())
                .asString();

        assertEquals(400, joinOpenTeamResponse.getStatus());
        assertEquals("Team is full!", joinOpenTeamResponse.getBody());


        // Get invitations for player5
        HttpResponse<String> myPendingInvitationsResonse = Unirest.get(createURLWithPort("/api/invitation/pending"))
                .header("Content-Type", "application/json")
                .asString();

        assertEquals(200, myPendingInvitationsResonse.getStatus());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode invitationJson = mapper.readTree(myPendingInvitationsResonse.getBody());

        String invitationID = invitationJson.get(0).get("id").asText();

        String invitationTeamName = invitationJson.get(0).get("team").get("name").asText();
        Long invitationTeamID = invitationJson.get(0).get("team").get("id").asLong();

        assertEquals(playerThreeTeam[0], invitationTeamName);
        assertEquals(teamRepository.findByName((String) playerThreeTeam[0]).getID(), invitationTeamID);


        // Try to join teamThree as player5
        ObjectMapper acceptInviteBodyMapper = new ObjectMapper();
        ObjectNode acceptInviteBody = acceptInviteBodyMapper.createObjectNode();

        acceptInviteBody.put("idInvitation", invitationID);

        HttpResponse<String> acceptInvitationResponse = Unirest.post(createURLWithPort("/api/invitation/accept"))
                .header("Content-Type", "application/json")
                .body(acceptInviteBody.toString())
                .asString();


        assertEquals(400, acceptInvitationResponse.getStatus());
        assertEquals("The team is full, you can no longer join.", acceptInvitationResponse.getBody());
    }


    @AfterAll
    public void cleanUp(){
        Unirest.shutDown();
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}


