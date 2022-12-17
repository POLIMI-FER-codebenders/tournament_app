package dsd.codebenders.tournament_app.integration.authentication;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dsd.codebenders.tournament_app.dao.PlayerRepository;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.services.PlayerService;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.json.JSONException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LogoutTest {

    @LocalServerPort
    private int port;

    @Autowired
    private PlayerService playerService;
    @Autowired
    private PlayerRepository playerRepository;

    private Player hrvoje = new Player("hrvoje459", "hrvoje@hrvoje.hr", "testTestT1");

    @Test
    @Order(1)
    void logoutTest() throws JSONException, JsonProcessingException {

        playerRepository.deleteAll();
        playerService.addNewPlayer(hrvoje);

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


        HttpResponse<String> logoutSuccess = Unirest.get(createURLWithPort("/authentication/logout")).asString();

        HttpResponse<String> getUnauthenticated = Unirest.get(createURLWithPort("/api/player/get"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .asString();

        assertEquals(200, logoutSuccess.getStatus());
        assertEquals("You are not authenticated!", getUnauthenticated.getBody());

    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
