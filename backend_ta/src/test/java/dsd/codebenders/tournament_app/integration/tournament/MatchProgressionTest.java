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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import java.util.Objects;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;


@TestPropertySource(locations = "classpath:application-integration.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MatchProgressionTest {

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

    @Value("${tournament-app.tournament.class-selection-time-duration:60}")
    private int classSelectionTimeDuration;
    @Value("${tournament-app.tournament-match.break-time-duration:10}")
    private int breakTimeDuration;
    @Value("${tournament-app.tournament-match.phase-one-duration:10}")
    private int phaseOneDuration;
    @Value("${tournament-app.tournament-match.phase-two-duration:10}")
    private int phaseTwoDuration;
    @Value("${tournament-app.tournament-match.phase-three-duration:10}")
    private int phaseThreeDuration;
    @Value("${testing.buffer_time:5}")
    private long testingBufferTime;


    private Player teamThreeLeader = new Player("playerFiveLeader", "pfl@pfl.pl", "testTestT1");
    private Player teamFourLeader = new Player("playerSixLeader", "psl@psl.pl", "testTestT1");

    private String[] teamThreeInfo = {"TeamThree", "1", "OPEN"};
    private String[] teamFourInfo = {"TeamFour", "1", "OPEN"};

    // {name, teamSize, numberOfTeams, type, matchType}
    private String[] fourthTournamentInfo = {"fourthTournament", "1", "2", "KNOCKOUT", "MULTIPLAYER"};


    Long matchId = -1L;
    Long tournamentId = -1L;

    @Test
    @Order(1)
    void addPlayersAndTeams(){
        playerService.addNewPlayer(teamThreeLeader);
        playerService.addNewPlayer(teamFourLeader);

        // Login as teamThreeLeader
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", teamThreeLeader.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // Create TeamThree
        ObjectMapper teamMapper = new ObjectMapper();
        ObjectNode team = teamMapper.createObjectNode();

        team.put("name", teamThreeInfo[0]);
        team.put("maxNumberOfPlayers", Integer.parseInt(teamThreeInfo[1]));
        team.put("policy", teamThreeInfo[2]);

        HttpResponse<String> createTeamResponse = Unirest.post(createURLWithPort("/api/team/create"))
                .header("Content-Type", "application/json")
                .body(team.toString())
                .asString();

        assertEquals(200, createTeamResponse.getStatus());

        // As user teamThreeLeader create tournament with team size 1
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

        tournamentId = tournamentService.getActiveTournamentByName(fourthTournamentInfo[0]).get().getID();

        // As teamThreeLeader join tournament
        ObjectMapper tournamentBodyMapper = new ObjectMapper();
        ObjectNode tournamentBody = tournamentBodyMapper.createObjectNode();

        tournamentBody.put("idTournament", tournamentService.getActiveTournamentByName(fourthTournamentInfo[0]).get().getID());

        HttpResponse<String> successfulJoinTournamentResponse = Unirest.post(createURLWithPort("/api/tournament/join"))
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

        // Create TeamFour
        teamMapper = new ObjectMapper();
        team = teamMapper.createObjectNode();

        team.put("name", teamFourInfo[0]);
        team.put("maxNumberOfPlayers", Integer.parseInt(teamFourInfo[1]));
        team.put("policy", teamFourInfo[2]);

        createTeamResponse = Unirest.post(createURLWithPort("/api/team/create"))
                .header("Content-Type", "application/json")
                .body(team.toString())
                .asString();

        assertEquals(200, createTeamResponse.getStatus());

        // As teamFourLeader join tournament
        tournamentBodyMapper = new ObjectMapper();
        tournamentBody = tournamentBodyMapper.createObjectNode();

        tournamentBody.put("idTournament", tournamentService.getActiveTournamentByName(fourthTournamentInfo[0]).get().getID());

        successfulJoinTournamentResponse = Unirest.post(createURLWithPort("/api/tournament/join"))
                .header("Content-Type", "application/json")
                .body(tournamentBody.toString())
                .asString();

        assertEquals(200, successfulJoinTournamentResponse.getStatus());


        Unirest.shutDown();
    }


    @Test
    @Order(2)
    void checkMatchCreated() throws TimeoutException, JsonProcessingException {
        long start = System.currentTimeMillis();


        while(tournamentService.getActiveTournamentByName(fourthTournamentInfo[0]).get().getStatus().toString() == "SELECTING_CLASSES"){

            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            if(timeElapsed > ((classSelectionTimeDuration + breakTimeDuration + testingBufferTime) * 1000)){

                throw new TimeoutException("Kasnis brte" + timeElapsed + tournamentService.getActiveTournamentByName(fourthTournamentInfo[0]).get().getStatus().toString());
            }
        }


        // Login as teamThreeLeader
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", teamThreeLeader.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        // check that there is a match scheduled and created on codedefenders
        HttpResponse<String> getMatchResponse = Unirest.get(createURLWithPort("/api/match/current_match"))
                .header("Content-Type", "application/json")
                .asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(getMatchResponse.getBody());

        String result = response.get("result").asText();
        String server = response.get("server").asText();
        String cdId = response.get("cdId").asText();
        String token = response.get("token").asText();
        Integer phaseOneDurationResponse = response.get("phaseOneDuration").asInt();
        Integer phaseTwoDurationResponse = response.get("phaseTwoDuration").asInt();
        Integer phaseThreeDurationResponse = response.get("phaseThreeDuration").asInt();

        assertEquals("ongoing match found", result);
        assertEquals(false, server.isEmpty());
        assertEquals(false, cdId.isEmpty());
        assertEquals(false, token.isEmpty());
        assertEquals(phaseOneDuration, phaseOneDurationResponse);
        assertEquals(phaseTwoDuration, phaseTwoDurationResponse);
        assertEquals(phaseThreeDuration, phaseThreeDurationResponse);
    }

    @Test
    @Order(3)
    void checkMatchStatusProgression() throws TimeoutException, JsonProcessingException {
        // Login as teamThreeLeader
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", teamThreeLeader.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);


        matchId = matchService.getOngoingMatchByPlayer(teamFourLeader).getID();


        long start = System.currentTimeMillis();
        while(matchService.findById(matchId).get().getStatus().toString() == "CREATED"){

            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            if(timeElapsed > ((breakTimeDuration + testingBufferTime) * 1000)){
                throw new TimeoutException("Kasnis brte" + timeElapsed + tournamentService.getActiveTournamentByName(fourthTournamentInfo[0]).get().getStatus().toString());
            }
        }

        HttpResponse<String> matchScoreStatusResponse = Unirest.get(createURLWithPort("/streaming/score?matchId="+matchId))
                .asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode matchResponseBody = mapper.readTree(matchScoreStatusResponse.getBody());

        assertEquals("IN_PHASE_ONE", matchResponseBody.get("status").asText());


        start = System.currentTimeMillis();
        while(matchService.findById(matchId).get().getStatus().toString() == "IN_PHASE_ONE"){

            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            if(timeElapsed > ((phaseOneDuration + testingBufferTime) * 1000)){
                throw new TimeoutException("Kasnis brte" + timeElapsed + tournamentService.getActiveTournamentByName(fourthTournamentInfo[0]).get().getStatus().toString());
            }
        }

        matchScoreStatusResponse = Unirest.get(createURLWithPort("/streaming/score?matchId="+matchId))
                .asString();

        mapper = new ObjectMapper();
        matchResponseBody = mapper.readTree(matchScoreStatusResponse.getBody());

        assertEquals("IN_PHASE_TWO", matchResponseBody.get("status").asText());


        start = System.currentTimeMillis();
        while(matchService.findById(matchId).get().getStatus().toString() == "IN_PHASE_TWO"){

            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            if(timeElapsed > ((phaseTwoDuration + testingBufferTime) * 1000)){
                throw new TimeoutException("Kasnis brte" + timeElapsed + tournamentService.getActiveTournamentByName(fourthTournamentInfo[0]).get().getStatus().toString());
            }
        }

        matchScoreStatusResponse = Unirest.get(createURLWithPort("/streaming/score?matchId="+matchId))
                .asString();

        mapper = new ObjectMapper();
        matchResponseBody = mapper.readTree(matchScoreStatusResponse.getBody());

        assertEquals("IN_PHASE_THREE", matchResponseBody.get("status").asText());


        start = System.currentTimeMillis();
        while(matchService.findById(matchId).get().getStatus().toString() == "IN_PHASE_THREE"){

            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            if(timeElapsed > ((phaseThreeDuration + testingBufferTime) * 1000)){
                throw new TimeoutException("Kasnis brte" + timeElapsed + tournamentService.getActiveTournamentByName(fourthTournamentInfo[0]).get().getStatus().toString());
            }
        }


        matchScoreStatusResponse = Unirest.get(createURLWithPort("/streaming/score?matchId="+matchId))
                .asString();

        mapper = new ObjectMapper();
        matchResponseBody = mapper.readTree(matchScoreStatusResponse.getBody());

        assertEquals("ENDED", matchResponseBody.get("status").asText());
    }

    @Test
    @Order(4)
    void checkMatchWinnerExists() throws InterruptedException, TimeoutException, JsonProcessingException {

        Long start = System.currentTimeMillis();
        while(matchService.findById(matchId).get().getWinningTeam() == null){
            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            if(timeElapsed > ((phaseThreeDuration + testingBufferTime) * 1000)){
                throw new TimeoutException("Kasnis brte" + timeElapsed + tournamentService.getActiveTournamentByName(fourthTournamentInfo[0]).get().getStatus().toString());
            }
        }

        assertEquals(false, Objects.isNull(matchService.findById(matchId).get().getWinningTeam()));
    }

    @AfterAll
    public void cleanUp(){
        Unirest.shutDown();
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
