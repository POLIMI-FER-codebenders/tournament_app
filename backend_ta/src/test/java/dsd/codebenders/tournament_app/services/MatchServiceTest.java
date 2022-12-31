package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.MatchRepository;
import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.entities.*;
import dsd.codebenders.tournament_app.entities.utils.MatchStatus;
import dsd.codebenders.tournament_app.entities.utils.MatchType;
import dsd.codebenders.tournament_app.entities.utils.TeamPolicy;
import dsd.codebenders.tournament_app.entities.utils.TournamentType;
import dsd.codebenders.tournament_app.errors.CDServerUnreachableException;
import dsd.codebenders.tournament_app.errors.MatchCreationException;
import dsd.codebenders.tournament_app.requests.CDClassUploadRequest;
import dsd.codebenders.tournament_app.utils.HTTPRequestsSender;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureMockMvc
class MatchServiceTest {

    @Autowired
    private MatchService matchService;

    @MockBean
    private ServerService serverService;

    @MockBean
    private CDPlayerService cdPlayerService;

    @MockBean
    private RoundClassChoiceService roundClassChoiceService;

    @MockBean
    private CDGameClassService cdGameClassService;

    @MockBean
    private MatchRepository matchRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    private Team team1;
    private Team team2;
    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;



    @Test
    void createMatchOnCD() throws CDServerUnreachableException, MatchCreationException {

        team1 = new Team("team1", 2, null, TeamPolicy.CLOSED, false, LocalDate.now());
        player1 = new Player("player1", "player1@gmail.com", "pass");
        player1.setIsAdmin(false);
        player2 = new Player("player2", "player2@gmail.com", "pass");
        player2.setIsAdmin(false);
        playerRepository.save(player1);
        playerRepository.save(player2);
        player1.setTeam(team1);
        player2.setTeam(team1);
        team1.setCreator(player1);
        team1.addTeamMember(player1);
        team1.addTeamMember(player2);
        teamRepository.save(team1);
        playerRepository.save(player1);
        playerRepository.save(player2);

        // second team
        team2 = new Team("team2", 2, null, TeamPolicy.CLOSED, false, LocalDate.now());
        player3 = new Player("player3", "player3@gmail.com", "pass");
        player3.setIsAdmin(false);
        player4 = new Player("player4", "player4@gmail.com", "pass");
        player4.setIsAdmin(false);
        playerRepository.save(player3);
        playerRepository.save(player4);
        player3.setTeam(team2);
        player4.setTeam(team2);
        team2.setCreator(player3);
        team2.addTeamMember(player3);
        team2.addTeamMember(player4);
        teamRepository.save(team2);
        playerRepository.save(player3);
        playerRepository.save(player4);

        // create the tournament
        Tournament tournament = new KnockoutTournament();
        tournament.setName("TournamentTest");
        tournament.setCreator(player1);
        tournament.setNumberOfTeams(2);
        tournament.setTeamSize(2);
        tournament.setType(TournamentType.KNOCKOUT);
        tournament.setMatchType(MatchType.MULTIPLAYER);

        Server server = new Server();
        Mockito.when(serverService.getCDServer()).thenReturn(server);

        Match match = new Match(team1, team2, 1, tournament, new Date());

        Mockito.when(cdPlayerService.getCDPlayerByServer(Mockito.any(), Mockito.any())).thenReturn(null);
        Mockito.doNothing().when(cdPlayerService).addNewCDPlayer(Mockito.any());

        GameClass gameClass = Mockito.mock(GameClass.class);
        Mockito.when(gameClass.getData()).thenReturn("public class Test {}".getBytes());
        Mockito.when(gameClass.getFilename()).thenReturn("file.java");
        RoundClassChoice roundClassChoice = new RoundClassChoice();
        roundClassChoice.setGameClass(gameClass);
        Mockito.when(roundClassChoiceService.getRoundClassChoiceByTournamentAndRound(Mockito.any(Tournament.class), Mockito.any(Integer.class))).thenReturn(roundClassChoice);

        try (MockedStatic<HTTPRequestsSender> httpRequestsSenderMockedStatic = Mockito.mockStatic(HTTPRequestsSender.class)){
            httpRequestsSenderMockedStatic.when(() -> HTTPRequestsSender.sendGetRequest(Mockito.eq(server), Mockito.eq("/admin/api/auth/newUser"), Mockito.any(), Mockito.eq(CDPlayer.class))).thenReturn(new CDPlayer());
            httpRequestsSenderMockedStatic.when(() -> HTTPRequestsSender.sendPostRequest(Mockito.eq(server), Mockito.eq("/admin/api/class/upload"), Mockito.any(), Mockito.eq(CDGameClass.class))).thenReturn(new CDGameClass());
            httpRequestsSenderMockedStatic.when(() -> HTTPRequestsSender.sendPostRequest(Mockito.eq(server), Mockito.eq("/admin/api/game"), Mockito.any(), Mockito.eq(Match.class))).thenReturn(match);

            matchService.createMatchOnCD(match);

            Mockito.verify(cdGameClassService).addNewCDGameClass(Mockito.any(CDGameClass.class));
            Mockito.verify(matchRepository).save(Mockito.any(Match.class));
            assertEquals(MatchStatus.CREATED, match.getStatus());
            assertEquals(server, match.getServer());
        }

    }

}
