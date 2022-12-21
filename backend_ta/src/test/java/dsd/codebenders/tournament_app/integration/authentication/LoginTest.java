package dsd.codebenders.tournament_app.integration.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.services.PlayerService;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoginTest {

    @LocalServerPort
    private int port;

    @Autowired
    private PlayerService playerService;
    @Autowired
    private PlayerRepository playerRepository;

    private Player hrvoje = new Player("hrvoje459", "hrvoje@hrvoje.hr", "testTestT1");

    @Test
    @Order(1)
    void unauthenticatedGetFailure() throws JSONException {

        HttpResponse<String> getUnauthenticated = Unirest.get(createURLWithPort("/api/player/get-all"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .asString();

        assertEquals(401, getUnauthenticated.getStatus());
        assertEquals("You are not authenticated!", getUnauthenticated.getBody());

    }

    @Test
    @Order(2)
    void loginFailureTest() throws JSONException {

        //playerRepository.deleteAll();
        playerService.addNewPlayer(hrvoje);

        HttpResponse<String> loginFailure = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", "hrvoje459")
                .field("password", "testtestt1")
                .asString();

        String location = loginFailure.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() -7 );

        HttpResponse<String> getUnauthenticated = Unirest.get(createURLWithPort("/api/player/get"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .asString();

        assertEquals("failure", location);
        assertEquals("You are not authenticated!", getUnauthenticated.getBody());

    }

    @Test
    @Order(3)
    void loginSuccessfulTest() throws JSONException, JsonProcessingException {

        //playerRepository.deleteAll();
        //playerService.addNewPlayer(hrvoje);

        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", "hrvoje459")
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() -7 );

        HttpResponse<String> getAuthenticated = Unirest.get(createURLWithPort("/api/player/get"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .asString();

        ObjectMapper mapper = new ObjectMapper();
        String username = mapper.readTree(getAuthenticated.getBody()).findValuesAsText("username").get(0);

        assertEquals("success", location);
        assertEquals("hrvoje459", username);

    }

    @Test
    @Order(4)
    void logoutTest() throws JSONException, JsonProcessingException {

        // we are logged in because of loginSuccessfulTest() so we dont have to login in this test

        HttpResponse<String> getAuthenticated = Unirest.get(createURLWithPort("/api/player/get"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .asString();

        ObjectMapper mapper = new ObjectMapper();
        String username = mapper.readTree(getAuthenticated.getBody()).findValuesAsText("username").get(0);


        assertEquals("hrvoje459", username);


        HttpResponse<String> logoutSuccess = Unirest.get(createURLWithPort("/authentication/logout")).asString();

        HttpResponse<String> getUnauthenticated = Unirest.get(createURLWithPort("/api/player/get"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .asString();

        assertEquals(200, logoutSuccess.getStatus());
        assertEquals("You are not authenticated!", getUnauthenticated.getBody());

    }

    @AfterAll
    public void cleanUp(){
        playerRepository.delete(hrvoje);
        Unirest.shutDown();
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
