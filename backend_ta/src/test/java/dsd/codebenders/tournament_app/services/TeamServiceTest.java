package dsd.codebenders.tournament_app.services;

import java.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.dao.RoundClassChoiceRepository;
import dsd.codebenders.tournament_app.dao.TeamRepository;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.entities.utils.TeamPolicy;
import dsd.codebenders.tournament_app.entities.utils.TeamRole;
import dsd.codebenders.tournament_app.errors.BadRequestException;
import dsd.codebenders.tournament_app.errors.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureMockMvc
class TeamServiceTest {
    @Autowired
    TeamService teamService;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    TeamRepository teamRepository;
    private Team team;
    private Player player;
    private Player creator;
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void findById() {
        assertThrows(ResourceNotFoundException.class, () -> teamService.findById(1000L));
        playerRepository.save(creator);
        team.setCreator(creator);
        assertNotNull(teamService.findById(teamRepository.save(team).getID()));
    }

    @Test
    void createTeam() {
        playerRepository.save(creator);
        Team tempTeam = new Team("t", 1, creator, TeamPolicy.CLOSED, false, LocalDate.now());
        teamRepository.save(tempTeam);
        creator.setTeam(tempTeam);
        playerRepository.save(creator);
        assertThrows(BadRequestException.class, () -> teamService.createTeam(team, creator));
        creator.setTeam(null);
        playerRepository.save(creator);
        teamRepository.delete(tempTeam);
        team.setMaxNumberOfPlayers(1000);
        assertThrows(BadRequestException.class, () -> teamService.createTeam(team, creator));
        team.setMaxNumberOfPlayers(2);
        team = teamService.createTeam(team, creator);
        assertEquals(team.getCreator(), creator);
        assertTrue(team.getTeamMembers().contains(creator));
        assertEquals(creator.getTeam(), team);
        assertTrue(creator.getTeamsCreated().contains(team));
        teamRepository.flush();
        entityManager.refresh(team);
        entityManager.refresh(creator);
        assertEquals(team.getCreator(), creator);
        assertTrue(team.getTeamMembers().contains(creator));
        assertEquals(creator.getTeam(), team);
        assertTrue(creator.getTeamsCreated().contains(team));
        assertThrows(BadRequestException.class, () -> teamService.createTeam(team, creator));
    }

    @Test
    void joinTeam() {
        Team tempTeam1 = new Team("t", 1, creator, TeamPolicy.CLOSED, false, LocalDate.now());
        assertThrows(BadRequestException.class, () -> teamService.joinTeam(player, tempTeam1));
        Team tempTeam2 = new Team("t", 0, creator, TeamPolicy.OPEN, false, LocalDate.now());
        assertThrows(BadRequestException.class, () -> teamService.joinTeam(player, tempTeam2));
        Team tempTeam3 = new Team("t", 1, creator, TeamPolicy.OPEN, false, LocalDate.now());
        tempTeam3.setInTournament(true);
        assertThrows(BadRequestException.class, () -> teamService.joinTeam(player, tempTeam3));
        playerRepository.save(creator);
        team.setCreator(creator);
        teamRepository.save(team);
        playerRepository.save(player);
        teamService.joinTeam(player, team);
        assertTrue(team.getTeamMembers().contains(player));
        assertEquals(player.getTeam(), team);
        playerRepository.flush();
        entityManager.refresh(team);
        assertTrue(team.getTeamMembers().contains(player));
        assertEquals(player.getTeam(), team);
        assertThrows(BadRequestException.class, () -> teamService.joinTeam(player, team));
    }

    @Test
    void getAllMembers() {
        assertThrows(ResourceNotFoundException.class, () -> teamService.findById(1000L));
        playerRepository.save(player);
        playerRepository.save(creator);
        team.setCreator(creator);
        teamRepository.save(team);
        assertEquals(teamService.getAllMembers(team.getID()).size(), 0);
        player.setTeam(team);
        playerRepository.saveAndFlush(player);
        entityManager.refresh(team);
        assertEquals(teamService.getAllMembers(team.getID()).size(), 1);
    }

    @Test
    void leaveTeam() {
        playerRepository.save(creator);
        team.setCreator(creator);
        teamRepository.save(team);
        assertThrows(BadRequestException.class, () -> teamService.leaveTeam(player));
        player.setRole(TeamRole.LEADER);
        assertThrows(BadRequestException.class, () -> teamService.leaveTeam(player));
        player.setTeam(team);
        player.setRole(TeamRole.MEMBER);
        team.setInTournament(true);
        assertThrows(BadRequestException.class, () -> teamService.leaveTeam(player));
        team.setInTournament(false);
        playerRepository.saveAndFlush(player);
        entityManager.refresh(team);
        assertTrue(team.getTeamMembers().contains(player));
        teamService.leaveTeam(player);
        playerRepository.flush();
        assertFalse(team.getTeamMembers().contains(player));
        assertNull(player.getTeam());
        assertNull(player.getRole());
        entityManager.refresh(team);
        assertFalse(team.getTeamMembers().contains(player));
        assertNull(player.getTeam());
        assertNull(player.getRole());
    }

    @Test
    void kickMember() {
    }

    @Test
    void findAll() {
    }

    @Test
    void promoteToLeader() {
    }

    @BeforeEach
    void setUp() {
        team = new Team("a", 2, null, TeamPolicy.OPEN, false, LocalDate.now());
        player = new Player("p", "p", "p");
        player.setIsAdmin(false);
        creator = new Player("c", "c", "c");
        creator.setIsAdmin(false);
    }
}