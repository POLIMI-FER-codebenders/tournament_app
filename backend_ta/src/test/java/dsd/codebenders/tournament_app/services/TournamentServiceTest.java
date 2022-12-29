package dsd.codebenders.tournament_app.services;


import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.dao.TournamentRepository;
import dsd.codebenders.tournament_app.dao.TournamentScoreRepository;
import dsd.codebenders.tournament_app.entities.KnockoutTournament;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.entities.Tournament;
import dsd.codebenders.tournament_app.entities.utils.MatchType;
import dsd.codebenders.tournament_app.entities.utils.TeamPolicy;
import dsd.codebenders.tournament_app.entities.utils.TournamentStatus;
import dsd.codebenders.tournament_app.entities.utils.TournamentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureMockMvc
class TournamentServiceTest {

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TournamentRepository tournamentRepository;
    
    @Autowired
    private TournamentScoreRepository tournamentScoreRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    private Team team1;
    private Team team2;
    private Team team3;
    private Team team4;
    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;
    private Player player5;
    private Player player6;
    private Player player7;
    private Player player8;
    private Tournament tournament;

    @BeforeEach
    void setUp() {
            // first team
            team1 = new Team("team1", 2, null, TeamPolicy.CLOSED, false, LocalDate.now());
            player1 = new Player("player1", "player1@gmail.com", "pass");
            player1.setIsAdmin(false);
            player1.setTeam(team1);
            player2 = new Player("player2", "player2@gmail.com", "pass");
            player2.setIsAdmin(false);
            player2.setTeam(team1);
            playerRepository.save(player1);
            playerRepository.save(player2);
            team1.setCreator(player1);
            team1.addTeamMember(player1);
            team1.addTeamMember(player2);
            teamRepository.save(team1);
            // second team
            team2 = new Team("team2", 2, null, TeamPolicy.CLOSED, false, LocalDate.now());
            player3 = new Player("player3", "player3@gmail.com", "pass");
            player3.setIsAdmin(false);
            player3.setTeam(team2);
            player4 = new Player("player4", "player4@gmail.com", "pass");
            player4.setIsAdmin(false);
            player4.setTeam(team2);
            playerRepository.save(player3);
            playerRepository.save(player4);
            team2.setCreator(player3);
            team2.addTeamMember(player3);
            team2.addTeamMember(player4);
            teamRepository.save(team2);
            // third team
            team3 = new Team("team3", 2, null, TeamPolicy.CLOSED, false, LocalDate.now());
            player5 = new Player("player5", "player5@gmail.com", "pass");
            player5.setIsAdmin(false);
            player5.setTeam(team3);
            player6 = new Player("player6", "player6@gmail.com", "pass");
            player6.setIsAdmin(false);
            player6.setTeam(team3);
            playerRepository.save(player5);
            playerRepository.save(player6);
            team3.setCreator(player5);
            team3.addTeamMember(player5);
            team3.addTeamMember(player6);
            teamRepository.save(team3);
            // fourth team
            team4 = new Team("team4", 2, null, TeamPolicy.CLOSED, false, LocalDate.now());
            player7 = new Player("player7", "player7@gmail.com", "pass");
            player7.setIsAdmin(false);
            player7.setTeam(team4);
            player8 = new Player("player8", "player8@gmail.com", "pass");
            player8.setIsAdmin(false);
            player8.setTeam(team4);
            playerRepository.save(player7);
            playerRepository.save(player8);
            team4.setCreator(player7);
            team4.addTeamMember(player7);
            team4.addTeamMember(player8);
            teamRepository.save(team4);
    }

    @Test
    @Order(1)
    void createTournament() {
        // create the tournament
        tournament = new KnockoutTournament();
        tournament.setName("TournamentTest");
        tournament.setCreator(player1);
        tournament.setNumberOfTeams(4);
        tournament.setTeamSize(2);
        tournament.setType(TournamentType.KNOCKOUT);
        tournament.setMatchType(MatchType.MULTIPLAYER);

        tournamentService.createTournament(tournament, player1);

        assertTrue(tournamentRepository.findAll().contains(tournament));
    }

    @Test
    @Order(2)
    void addTeamsToTheTournament() {
        // create the tournament
        tournament = new KnockoutTournament();
        tournament.setName("TournamentTest");
        tournament.setCreator(player1);
        tournament.setNumberOfTeams(4);
        tournament.setTeamSize(2);
        tournament.setType(TournamentType.KNOCKOUT);
        tournament.setMatchType(MatchType.MULTIPLAYER);

        tournamentService.createTournament(tournament, player1);

        // add three teams to the tournament
        tournamentService.addTeam(tournament, team1);
        tournamentService.addTeam(tournament, team2);
        tournamentService.addTeam(tournament, team3);

        // verify that the corresponding entries in tournament score table have been created
        assertTrue(tournamentScoreRepository.findByTeamAndTournament(team1, tournament).isPresent());
        assertTrue(tournamentScoreRepository.findByTeamAndTournament(team2, tournament).isPresent());
        assertTrue(tournamentScoreRepository.findByTeamAndTournament(team3, tournament).isPresent());

        // verify that the teams have been set in tournament
        assertTrue(teamRepository.findByName(team1.getName()).isInTournament());
        assertTrue(teamRepository.findByName(team2.getName()).isInTournament());
        assertTrue(teamRepository.findByName(team3.getName()).isInTournament());

        // verify that the tournament is not started yet
        assertEquals(tournamentRepository.findById(tournament.getID()).get().getStatus(), TournamentStatus.TEAMS_JOINING);
    }

    @Test
    @Order(3)
    void addAllTeamsAndStartTournament() {
        // create the tournament
        tournament = new KnockoutTournament();
        tournament.setName("TournamentTest");
        tournament.setCreator(player1);
        tournament.setNumberOfTeams(4);
        tournament.setTeamSize(2);
        tournament.setType(TournamentType.KNOCKOUT);
        tournament.setMatchType(MatchType.MULTIPLAYER);

        tournamentService.createTournament(tournament, player1);

        // add three teams to the tournament
        tournamentService.addTeam(tournament, team1);
        tournamentService.addTeam(tournament, team2);
        tournamentService.addTeam(tournament, team3);
        tournamentService.addTeam(tournament, team4);

        // verify that the tournament is started
        assertEquals(tournamentRepository.findById(tournament.getID()).get().getStatus(), TournamentStatus.SELECTING_CLASSES);
    }

}
