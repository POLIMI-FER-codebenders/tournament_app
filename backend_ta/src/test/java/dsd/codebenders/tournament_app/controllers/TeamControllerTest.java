package dsd.codebenders.tournament_app.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Team;
import dsd.codebenders.tournament_app.entities.utils.TeamPolicy;
import dsd.codebenders.tournament_app.services.TeamService;
import dsd.codebenders.tournament_app.testUtils.TestRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class TeamControllerTest {

    @Autowired
    TestRunner testRunner;
    Player creator = new Player("a", "b", "c");
    Team team = new Team("t", 1, creator, TeamPolicy.CLOSED, false, LocalDate.now());
    @MockBean
    private TeamService teamService;

    @Test
    void getMyTeam() {
    }

    @Test
    void getTeam() throws Exception {
        testRunner.testGet("/api/team/get", Map.of("id", ""), List.of(status().isBadRequest()));
        testRunner.testGet("/api/team/get", Map.of("id", "1"));
    }

    @Test
    void getAllTeams() {
    }

    @Test
    void getAllMembers() {
    }

    @Test
    void createTeam() {
    }

    @Test
    void joinTeam() {
    }

    @Test
    void leaveTeam() {
    }

    @Test
    void kickMember() {
    }

    @Test
    void promoteToLeader() {
    }

    @BeforeEach
    void setUp() {
        doReturn(team).when(teamService).findById(any());
    }
}