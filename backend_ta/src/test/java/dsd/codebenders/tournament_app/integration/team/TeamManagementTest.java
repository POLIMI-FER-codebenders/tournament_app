package dsd.codebenders.tournament_app.integration.team;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.dao.ServerRepository;
import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.dao.TournamentRepository;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.services.PlayerService;
import dsd.codebenders.tournament_app.services.TeamService;
import dsd.codebenders.tournament_app.services.TournamentService;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestPropertySource(locations = "classpath:application-integration.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TeamManagementTest {

    @LocalServerPort
    private int port;

    @Autowired
    private PlayerService playerService;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TournamentService tournamentService;


    private Player player6 = new Player("player6", "player6@player6.pl", "testTestT1");
    private Player player7 = new Player("player7", "player7@player7.pl", "testTestT1");
    private Player player8 = new Player("player8", "player@player8.pl", "testTestT1");



    private String[] player6FirstTeam = {"TeamSixFirst", "2", "OPEN"};
    private String[] player6SecondTeam = {"TeamSixSecond", "2", "OPEN"};
    private String[] player6ThirdTeam = {"TeamSixThird", "2", "OPEN"};
    private String[] player8FourthTeam = {"TeamEightFourth", "2", "OPEN"};


    // {name, teamSize, numberOfTeams, type, matchType}
    private String[] player8TournamentInfo = {"player8tournament", "2", "2", "KNOCKOUT", "MULTIPLAYER"};


    @BeforeAll
    void addPlayersAndTeamsAndTournament() {
        // Add players
        playerService.addNewPlayer(player6);
        playerService.addNewPlayer(player7);
        playerService.addNewPlayer(player8);

        // Login as player8
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", player8.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // As player8 create team with size of 2
        ObjectMapper teamMapper = new ObjectMapper();
        ObjectNode team = teamMapper.createObjectNode();

        team.put("name", player8FourthTeam[0]);
        team.put("maxNumberOfPlayers", Integer.parseInt(player8FourthTeam[1]));
        team.put("policy", player8FourthTeam[2]);

        HttpResponse<String> createTeamResponse = Unirest.post(createURLWithPort("/api/team/create"))
                .header("Content-Type", "application/json")
                .body(team.toString())
                .asString();

        assertEquals(200, createTeamResponse.getStatus());

        // As player8 create tournament with team size of 2

        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", player8TournamentInfo[0]);
        tournament.put("teamSize", Integer.parseInt(player8TournamentInfo[1]));
        tournament.put("numberOfTeams", Integer.parseInt(player8TournamentInfo[2]));
        tournament.put("type", player8TournamentInfo[3]);
        tournament.put("matchType", player8TournamentInfo[4]);

        HttpResponse<String> successfulTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();

        assertEquals(200, successfulTournamentCreationResponse.getStatus());
    }


    @Test
    @Order(1)
    void soloLeaderLeaveTeamTest(){
        // Login as player6
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", player6.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // As player6 create team with size of 2

        ObjectMapper teamMapper = new ObjectMapper();
        ObjectNode team = teamMapper.createObjectNode();

        team.put("name", player6FirstTeam[0]);
        team.put("maxNumberOfPlayers", Integer.parseInt(player6FirstTeam[1]));
        team.put("policy", player6FirstTeam[2]);

        HttpResponse<String> createTeamResponse = Unirest.post(createURLWithPort("/api/team/create"))
                .header("Content-Type", "application/json")
                .body(team.toString())
                .asString();

        assertEquals(200, createTeamResponse.getStatus());

        // As player6 try to leave team
        HttpResponse<String> leaveTeamResponse = Unirest.post(createURLWithPort("/api/team/leave"))
                .header("Content-Type", "application/json")
                .asString();

        assertEquals(200, leaveTeamResponse.getStatus());


        HttpResponse<String> getTeamResponse = Unirest.get(createURLWithPort("/api/team/get-mine"))
                .header("Content-Type", "application/json")
                .asString();

        assertEquals(200, getTeamResponse.getStatus());
        assertEquals(true, getTeamResponse.getBody().isEmpty());
    }

    @Test
    @Order(2)
    void soloLeaderJoinAnotherTeamTest() throws JsonProcessingException {
        // Login as player6
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", player6.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // As player6 create team with size of 2

        ObjectMapper teamMapper = new ObjectMapper();
        ObjectNode team = teamMapper.createObjectNode();

        team.put("name", player6SecondTeam[0]);
        team.put("maxNumberOfPlayers", Integer.parseInt(player6SecondTeam[1]));
        team.put("policy", player6SecondTeam[2]);

        HttpResponse<String> createTeamResponse = Unirest.post(createURLWithPort("/api/team/create"))
                .header("Content-Type", "application/json")
                .body(team.toString())
                .asString();

        assertEquals(200, createTeamResponse.getStatus());

        // As player6 try to join another team (player8Fourthteam)

        ObjectMapper teamJoinBodyMapper = new ObjectMapper();
        ObjectNode teamJoinBody = teamJoinBodyMapper.createObjectNode();

        teamJoinBody.put("idTeam", teamRepository.findByName((String) player8FourthTeam[0]).getID());

        HttpResponse<String> joinAnotherTeamResponse = Unirest.post(createURLWithPort("/api/team/join"))
                .header("Content-Type", "application/json")
                .body(teamJoinBody.toString())
                .asString();

        assertEquals(200, joinAnotherTeamResponse.getStatus());


        HttpResponse<String> getTeamResponse = Unirest.get(createURLWithPort("/api/team/get-mine"))
                .header("Content-Type", "application/json")
                .asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(getTeamResponse.getBody());

        String teamName = response.get("name").asText();

        assertEquals(200, getTeamResponse.getStatus());
        assertEquals(player8FourthTeam[0], teamName);
    }

    @Test
    @Order(3)
    void leaveTeamTest() throws JsonProcessingException {
        // Login as player6
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", player6.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        HttpResponse<String> leaveTeamResponse = Unirest.post(createURLWithPort("/api/team/leave"))
                .header("Content-Type", "application/json")
                .asString();

        assertEquals(200, leaveTeamResponse.getStatus());


        HttpResponse<String> getTeamResponse = Unirest.get(createURLWithPort("/api/team/get-mine"))
                .header("Content-Type", "application/json")
                .asString();

        assertEquals(200, getTeamResponse.getStatus());
        assertEquals(true, getTeamResponse.getBody().isEmpty());
    }


    @Test
    @Order(4)
    void setupPlayer6ThirdTeam() throws JsonProcessingException {
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", player6.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        ObjectMapper teamMapper = new ObjectMapper();
        ObjectNode team = teamMapper.createObjectNode();

        team.put("name", player6ThirdTeam[0]);
        team.put("maxNumberOfPlayers", Integer.parseInt(player6ThirdTeam[1]));
        team.put("policy", player6ThirdTeam[2]);

        HttpResponse<String> createTeamResponse = Unirest.post(createURLWithPort("/api/team/create"))
                .header("Content-Type", "application/json")
                .body(team.toString())
                .asString();

        assertEquals(200, createTeamResponse.getStatus());



        // As player7 join player6s team
        loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", player7.getUsername())
                .field("password", "testTestT1")
                .asString();

        location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);


        ObjectMapper teamJoinBodyMapper = new ObjectMapper();
        ObjectNode teamJoinBody = teamJoinBodyMapper.createObjectNode();

        teamJoinBody.put("idTeam", teamRepository.findByName((String) player6ThirdTeam[0]).getID());

        HttpResponse<String> joinTeamResponse = Unirest.post(createURLWithPort("/api/team/join"))
                .header("Content-Type", "application/json")
                .body(teamJoinBody.toString())
                .asString();

        assertEquals(200, joinTeamResponse.getStatus());

        HttpResponse<String> getTeamResponse = Unirest.get(createURLWithPort("/api/team/get-mine"))
                .header("Content-Type", "application/json")
                .asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(getTeamResponse.getBody());

        String teamName = response.get("name").asText();

        assertEquals(200, getTeamResponse.getStatus());
        assertEquals(player6ThirdTeam[0], teamName);

    }

    @Test
    @Order(5)
    void leaderLeaveTeamWithMembersTest(){
        // As player6 try leave
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", player6.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        HttpResponse<String> leaveTeamResponse = Unirest.post(createURLWithPort("/api/team/leave"))
                .header("Content-Type", "application/json")
                .asString();

        assertEquals(400, leaveTeamResponse.getStatus());
        assertEquals("The leader can't leave a team with other members", leaveTeamResponse.getBody());


        // As player6 try to join another team
        /*ObjectMapper teamJoinBodyMapper = new ObjectMapper();
        ObjectNode teamJoinBody = teamJoinBodyMapper.createObjectNode();

        teamJoinBody.put("idTeam", teamRepository.findByName((String) player8team[0]).getID());

        HttpResponse<String> joinAnotherTeamResponse = Unirest.post(createURLWithPort("/api/team/join"))
                .header("Content-Type", "application/json")
                .body(teamJoinBody.toString())
                .asString();

        assertEquals(200, joinAnotherTeamResponse.getStatus());*/


        //assertEquals(400, joinAnotherTeamResponse.getStatus());
        // assertEquals("The leader can't leave a team with other members", leaveTeamResponse.getBody());

    }

    @Test
    @Order(6)
    void kickTeamMemberTest() throws JsonProcessingException {
        // Login as player6
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", player6.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);


        // As player6 kick player7 out of team


        ObjectMapper kickedPlayerMapper = new ObjectMapper();
        ObjectNode kickedPlayer = kickedPlayerMapper.createObjectNode();

        kickedPlayer.put("idKickedPlayer", player7.getID());

        HttpResponse<String> kickResponse = Unirest.post(createURLWithPort("/api/team/kick-member"))
                .header("Content-Type", "application/json")
                .body(kickedPlayer.toString())
                .asString();

        assertEquals(200, kickResponse.getStatus());

        // Login as player7
        loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", player7.getUsername())
                .field("password", "testTestT1")
                .asString();

        location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // Check that player7 is not in a team
        HttpResponse<String> getTeamResponse = Unirest.get(createURLWithPort("/api/team/get-mine"))
                .header("Content-Type", "application/json")
                .asString();

        assertEquals(200, getTeamResponse.getStatus());
        assertEquals(true, getTeamResponse.getBody().isEmpty());

        // As player7 join player6s team again
        ObjectMapper teamJoinBodyMapper = new ObjectMapper();
        ObjectNode teamJoinBody = teamJoinBodyMapper.createObjectNode();

        teamJoinBody.put("idTeam", teamRepository.findByName((String) player6ThirdTeam[0]).getID());

        HttpResponse<String> joinTeamResponse = Unirest.post(createURLWithPort("/api/team/join"))
                .header("Content-Type", "application/json")
                .body(teamJoinBody.toString())
                .asString();

        assertEquals(200, joinTeamResponse.getStatus());

        getTeamResponse = Unirest.get(createURLWithPort("/api/team/get-mine"))
                .header("Content-Type", "application/json")
                .asString();

        ObjectMapper teamMapper = new ObjectMapper();
        JsonNode team = teamMapper.readTree(getTeamResponse.getBody());

        String teamName = team.get("name").asText();

        assertEquals(200, getTeamResponse.getStatus());
        assertEquals(player6ThirdTeam[0], teamName);
    }

    @Test
    @Order(7)
    void promoteMemberToLeaderTest(){
        // Login as player6
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", player6.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // As player6 promote player7 to team leader

        ObjectMapper promoteLeaderBodyMapper = new ObjectMapper();
        ObjectNode promoteLeaderBody = promoteLeaderBodyMapper.createObjectNode();

        promoteLeaderBody.put("idPlayer", player7.getID());

        HttpResponse<String> promoteToLeaderResponse = Unirest.post(createURLWithPort("/api/team/members/promote-leader"))
                .header("Content-Type", "application/json")
                .body(promoteLeaderBody.toString())
                .asString();

        assertEquals(200, promoteToLeaderResponse.getStatus());
        assertEquals("LEADER", playerService.findByUsername(player7.getUsername()).getRole().toString());

        // As player6 try to join player8 tournament

        ObjectMapper tournamentBodyMapper = new ObjectMapper();
        ObjectNode tournamentBody = tournamentBodyMapper.createObjectNode();

        tournamentBody.put("idTournament", tournamentService.getActiveTournamentByName(player8TournamentInfo[0]).get().getID());

        HttpResponse<String> joinTournamentResponse = Unirest.post(createURLWithPort("/api/tournament/join"))
                .header("Content-Type", "application/json")
                .body(tournamentBody.toString())
                .asString();

        assertEquals(400, joinTournamentResponse.getStatus());
        assertEquals("You are not the leader of the team", joinTournamentResponse.getBody());
    }


    @Test
    @Order(8)
    void joinTournamentLockTeamManagementTest() throws JsonProcessingException {
        // Login as player7
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", player7.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // As player7 try to join player8 tournament
        ObjectMapper tournamentBodyMapper = new ObjectMapper();
        ObjectNode tournamentBody = tournamentBodyMapper.createObjectNode();

        tournamentBody.put("idTournament", tournamentService.getActiveTournamentByName(player8TournamentInfo[0]).get().getID());

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


        assertEquals(true, teamRepository.findByName(player6ThirdTeam[0]).isInTournament());
        assertEquals(player8TournamentInfo[0], name);
        assertEquals(player8TournamentInfo[1], teamSize);
        assertEquals(player8TournamentInfo[2], numberOfTeams);
        assertEquals(player8TournamentInfo[3], type);
        assertEquals(player8TournamentInfo[4], matchType);

        // As player7 while team is in tournament try to promote player6 to team leader
        ObjectMapper promoteLeaderBodyMapper = new ObjectMapper();
        ObjectNode promoteLeaderBody = promoteLeaderBodyMapper.createObjectNode();

        promoteLeaderBody.put("idPlayer", player6.getID());

        HttpResponse<String> promoteToLeaderResponse = Unirest.post(createURLWithPort("/api/team/members/promote-leader"))
                .header("Content-Type", "application/json")
                .body(promoteLeaderBody.toString())
                .asString();


        assertEquals(400, promoteToLeaderResponse.getStatus());
        assertEquals("Can't promote members while team is in tournament.", promoteToLeaderResponse.getBody());
        assertEquals("MEMBER", playerService.findByUsername(player6.getUsername()).getRole().toString());
    }



    @AfterAll
    public void cleanUp(){
        Unirest.shutDown();
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
