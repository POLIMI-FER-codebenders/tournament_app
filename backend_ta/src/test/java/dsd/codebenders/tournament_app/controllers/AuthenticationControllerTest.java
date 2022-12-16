package dsd.codebenders.tournament_app.controllers;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.services.PlayerService;
import dsd.codebenders.tournament_app.testUtils.TestRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class AuthenticationControllerTest {
    @Autowired
    TestRunner testRunner;
    @MockBean
    private PlayerService playerService;

    @Test
    void registerValid() throws Exception {
        String response =
                testRunner.testPost("/authentication/register", new Player("mario", "mario@gmail.com", "MarioPassword123")).andReturn().getResponse().getContentAsString();
        assertEquals(new ObjectMapper().readValue(response, HashMap.class).get("result"), "Registered");
    }

    @Test
    void registerDuplicateName() throws Exception {
        doReturn(true).when(playerService).checkUsernameAlreadyTaken(any());
        String response =
                testRunner.testPost("/authentication/register", new Player("mario", "mario@gmail.com", "MarioPassword123")).andReturn().getResponse().getContentAsString();
        assertEquals(new ObjectMapper().readValue(response, HashMap.class).get("result"), "Username already taken");
    }

    @Test
    void registerDuplicateMail() throws Exception {
        doReturn(true).when(playerService).checkEmailAlreadyTaken(any());
        String response =
                testRunner.testPost("/authentication/register", new Player("mario", "mario@gmail.com", "MarioPassword123")).andReturn().getResponse().getContentAsString();
        assertEquals(new ObjectMapper().readValue(response, HashMap.class).get("result"), "Email already taken");
    }

    @Test
    void registerInvalidName() throws Exception {
        testRunner.testPost("/authentication/register", new Player("Invalid name", "mario@gmail.com", "MarioPassword123"), List.of(status().isBadRequest()));
    }

    @Test
    void registerInvalidEmail() throws Exception {
        testRunner.testPost("/authentication/register", new Player("mario", "Invalid email", "MarioPassword123"), List.of(status().isBadRequest()));
    }

    @Test
    void registerInvalidPassword() throws Exception {
        testRunner.testPost("/authentication/register", new Player("mario", "mario@gmail.com", "Invalid password"), List.of(status().isBadRequest()));
    }

    @BeforeEach
    void setUp() {
        doReturn(false).when(playerService).checkUsernameAlreadyTaken(any());
        doReturn(false).when(playerService).checkUsernameAlreadyTaken(any());
        doReturn(false).when(playerService).checkEmailAlreadyTaken(any());
        doNothing().when(playerService).addNewPlayer(any());
    }
}