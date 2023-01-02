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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@TestPropertySource(locations = "classpath:application-integration.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TournamentProgressionTest {


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


    private Player teamFiveLeader = new Player("playerSevenLeader", "psevl@psevl.pl", "testTestT1");
    private Player teamSixLeader = new Player("playerEightLeader", "pel@pel.pl", "testTestT1");
    private Player teamSevenLeader = new Player("playerNineLeader", "pnl@pnl.pl", "testTestT1");
    private Player teamEightLeader = new Player("playerTenLeader", "ptl2@ptl2.pl", "testTestT1");

    private String[] teamFiveInfo = {"TeamFive", "1", "OPEN"};
    private String[] teamSixInfo = {"TeamSix", "1", "OPEN"};
    private String[] teamSevenInfo = {"TeamSeven", "1", "OPEN"};
    private String[] teamEightInfo = {"TeamEight", "1", "OPEN"};

    // {name, teamSize, numberOfTeams, type, matchType}
    private String[] fourthTournamentInfo = {"fifthTournament", "1", "4", "KNOCKOUT", "MULTIPLAYER"};

    Long tournamentID = -1L;


    @Test
    @Order(1)
    void addPlayersAndTeams(){
        playerService.addNewPlayer(teamFiveLeader);
        playerService.addNewPlayer(teamSixLeader);
        playerService.addNewPlayer(teamSevenLeader);
        playerService.addNewPlayer(teamEightLeader);

        // Login as teamFiveLeader
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", teamFiveLeader.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // Create TeamFive
        ObjectMapper teamMapper = new ObjectMapper();
        ObjectNode team = teamMapper.createObjectNode();

        team.put("name", teamFiveInfo[0]);
        team.put("maxNumberOfPlayers", Integer.parseInt(teamFiveInfo[1]));
        team.put("policy", teamFiveInfo[2]);

        HttpResponse<String> createTeamResponse = Unirest.post(createURLWithPort("/api/team/create"))
                .header("Content-Type", "application/json")
                .body(team.toString())
                .asString();

        assertEquals(200, createTeamResponse.getStatus());

        // As user teamFiveLeader create tournament with team size 1
        ObjectMapper tournamentMapper = new ObjectMapper();
        ObjectNode tournament = tournamentMapper.createObjectNode();

        tournament.put("name", fourthTournamentInfo[0]);
        tournament.put("teamSize", Integer.parseInt(fourthTournamentInfo[1]));
        tournament.put("numberOfTeams", Integer.parseInt(fourthTournamentInfo[2]));
        tournament.put("type", fourthTournamentInfo[3]);
        tournament.put("matchType", fourthTournamentInfo[4]);

        HttpResponse<String> successfulTournamentCreationResponse = Unirest.post(createURLWithPort("/api/tournament/create"))
                .header("Content-Type", "application/json")
                .body(tournament.toString())
                .asString();

        assertEquals(200, successfulTournamentCreationResponse.getStatus());



        // As teamFiveLeader join tournament
        ObjectMapper tournamentBodyMapper = new ObjectMapper();
        ObjectNode tournamentBody = tournamentBodyMapper.createObjectNode();

        tournamentID = tournamentService.getActiveTournamentByName(fourthTournamentInfo[0]).get().getID();

        tournamentBody.put("idTournament", tournamentID);

        HttpResponse<String> successfulJoinTournamentResponse = Unirest.post(createURLWithPort("/api/tournament/join"))
                .header("Content-Type", "application/json")
                .body(tournamentBody.toString())
                .asString();

        assertEquals(200, successfulJoinTournamentResponse.getStatus());

        // Login as teamSixLeader
        loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", teamSixLeader.getUsername())
                .field("password", "testTestT1")
                .asString();

        location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // Create teamSixInfo
        teamMapper = new ObjectMapper();
        team = teamMapper.createObjectNode();

        team.put("name", teamSixInfo[0]);
        team.put("maxNumberOfPlayers", Integer.parseInt(teamSixInfo[1]));
        team.put("policy", teamSixInfo[2]);

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





        // Login as teamSevenLeader
        loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", teamSevenLeader.getUsername())
                .field("password", "testTestT1")
                .asString();

        location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // Create teamSeven
        teamMapper = new ObjectMapper();
        team = teamMapper.createObjectNode();

        team.put("name", teamSevenInfo[0]);
        team.put("maxNumberOfPlayers", Integer.parseInt(teamSevenInfo[1]));
        team.put("policy", teamSevenInfo[2]);

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
System.out.println(successfulJoinTournamentResponse.getBody());
        assertEquals(200, successfulJoinTournamentResponse.getStatus());



        // Login as teamEightLeader
        loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", teamEightLeader.getUsername())
                .field("password", "testTestT1")
                .asString();

        location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // Create teamSixInfo
        teamMapper = new ObjectMapper();
        team = teamMapper.createObjectNode();

        team.put("name", teamEightInfo[0]);
        team.put("maxNumberOfPlayers", Integer.parseInt(teamEightInfo[1]));
        team.put("policy", teamEightInfo[2]);

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





        Unirest.shutDown();
    }



    @Test
    @Order(2)
    void checkTournamentProgression() throws InterruptedException, JsonProcessingException {
        // Login as teamFiveLeader
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", teamFiveLeader.getUsername())
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
    @Order(3)
    void checkNextRoundCorrectTeams() throws JsonProcessingException {
        // Login as teamFiveLeader
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", teamFiveLeader.getUsername())
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

        JsonNode matchOne = tournamentResponse.get(0).get("matches").get(0);
        JsonNode matchTwo = tournamentResponse.get(0).get("matches").get(1);
        JsonNode matchFinal = tournamentResponse.get(0).get("matches").get(2);

        String winningTeamOne = matchOne.get("winningTeam").get("name").asText();
        String winningTeamTwo = matchTwo.get("winningTeam").get("name").asText();

        String finalAttackersTeam = matchFinal.get("attackersTeam").get("name").asText();
        String finalDefendersTeam = matchFinal.get("defendersTeam").get("name").asText();

        String finalWinningTeam = matchFinal.get("winningTeam").get("name").asText();

        assertEquals(true, (finalAttackersTeam.equals(winningTeamOne) ^ finalAttackersTeam.equals(winningTeamTwo))
                &&
                (finalDefendersTeam.equals(winningTeamOne) ^ finalDefendersTeam.equals(winningTeamTwo))
                &&
                (finalWinningTeam.equals(finalAttackersTeam) ^ finalWinningTeam.equals(finalDefendersTeam))
        );
    }

    @AfterAll
    public void cleanUp(){
        Unirest.shutDown();
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
