package dsd.codebenders.tournament_app.integration.server;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.services.PlayerService;
import dsd.codebenders.tournament_app.services.ServerService;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestPropertySource(locations = "classpath:application-integration.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CDServerManagementTest {

    @LocalServerPort
    private int port;

    @Autowired
    private PlayerService playerService;
    @Autowired
    private ServerService serverService;
    @Value("${tournament-app.admin.password:admin}")
    private String adminPassword;

    private Player regularPlayer = new Player("regularPlayer", "reg@play.pl", "testTestT1");
    private String cdServerAddress = "https://codedef2.duckdns.org";
    @Value("${code-defenders.default-servers.token:}")
    private String cdServerToken;

    @Test
    @Order(1)
    void adminUserLogin(){
        playerService.addNewPlayer(regularPlayer);

        // Login as ADMIN
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", "admin")
                .field("password", adminPassword)
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);
    }

    @Test
    @Order(2)
    void deleteCodeDefendersServer(){
        Integer numberOfServersBeforeDeletion = serverService.getAllActiveServers().size();

        ObjectMapper serverMapper = new ObjectMapper();
        ObjectNode server = serverMapper.createObjectNode();

        server.put("address", cdServerAddress);

        HttpResponse<String> deleteServerResponse = Unirest.post(createURLWithPort("/admin/api/cd-server/delete"))
                .header("Content-Type", "application/json")
                .body(server.toString())
                .asString();

        Integer numberOfServersAfterDeletion = serverService.getAllActiveServers().size();

        assertEquals(200, deleteServerResponse.getStatus());
        assertEquals(true, numberOfServersBeforeDeletion > numberOfServersAfterDeletion);


    }

    @Test
    @Order(3)
    void addCodeDefendersServer(){
        Integer numberOfServersBeforeAdding = serverService.getAllActiveServers().size();

        ObjectMapper serverMapper = new ObjectMapper();
        ObjectNode server = serverMapper.createObjectNode();

        server.put("address", cdServerAddress);
        server.put("adminToken", cdServerToken);

        HttpResponse<String> addServerResponse = Unirest.post(createURLWithPort("/admin/api/cd-server/register"))
                .header("Content-Type", "application/json")
                .body(server.toString())
                .asString();

        Integer numberOfServersAfterAdding = serverService.getAllActiveServers().size();

        assertEquals(200, addServerResponse.getStatus());
        assertEquals(true, numberOfServersAfterAdding > numberOfServersBeforeAdding);
    }

    @Test
    @Order(4)
    void updateCodeDefendersServerToken(){
        ObjectMapper serverMapper = new ObjectMapper();
        ObjectNode server = serverMapper.createObjectNode();

        server.put("address", cdServerAddress);
        server.put("adminToken", cdServerToken);

        HttpResponse<String> updateServerResponse = Unirest.post(createURLWithPort("/admin/api/cd-server/update"))
                .header("Content-Type", "application/json")
                .body(server.toString())
                .asString();

        assertEquals(200, updateServerResponse.getStatus());
    }

    @Test
    @Order(5)
    void deleteNonExistantCodeDefendersServer(){
        ObjectMapper serverMapper = new ObjectMapper();
        ObjectNode server = serverMapper.createObjectNode();

        server.put("address", "http://nonExistantcodedefenders.server");

        HttpResponse<String> deleteServerResponse = Unirest.post(createURLWithPort("/admin/api/cd-server/delete"))
                .header("Content-Type", "application/json")
                .body(server.toString())
                .asString();

        assertEquals(400, deleteServerResponse.getStatus());
        assertEquals("Server not found", deleteServerResponse.getBody());
    }

    @Test
    @Order(6)
    void addAlreadyRegisteredCodeDefendersServer(){
        ObjectMapper serverMapper = new ObjectMapper();
        ObjectNode server = serverMapper.createObjectNode();

        server.put("address", cdServerAddress);
        server.put("adminToken", cdServerToken);

        HttpResponse<String> addServerResponse = Unirest.post(createURLWithPort("/admin/api/cd-server/register"))
                .header("Content-Type", "application/json")
                .body(server.toString())
                .asString();

        assertEquals(400, addServerResponse.getStatus());
        assertEquals("Server already registered", addServerResponse.getBody());
    }

    @Test
    @Order(7)
    void invalidDeleteCodeDefendersServer(){
        ObjectMapper serverMapper = new ObjectMapper();
        ObjectNode server = serverMapper.createObjectNode();


        HttpResponse<String> deleteServerResponse = Unirest.post(createURLWithPort("/admin/api/cd-server/delete"))
                .header("Content-Type", "application/json")
                .body(server.toString())
                .asString();

        assertEquals(400, deleteServerResponse.getStatus());
        assertEquals("Address is missing", deleteServerResponse.getBody());


        // Login as regularPlayer
        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", regularPlayer.getUsername())
                .field("password", "testTestT1")
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);


        serverMapper = new ObjectMapper();
        server = serverMapper.createObjectNode();

        server.put("address", cdServerAddress);

        deleteServerResponse = Unirest.post(createURLWithPort("/admin/api/cd-server/delete"))
                .header("Content-Type", "application/json")
                .body(server.toString())
                .asString();

        assertEquals(401, deleteServerResponse.getStatus());
        assertEquals("Admin rights required", deleteServerResponse.getBody());
    }

    @Test
    @Order(8)
    void invalidRequest(){
        ObjectMapper serverMapper = new ObjectMapper();
        ObjectNode server = serverMapper.createObjectNode();

        HttpResponse<String> addServerResponse = Unirest.post(createURLWithPort("/admin/api/cd-server/register"))
                .header("Content-Type", "application/json")
                .body(server.toString())
                .asString();

        assertEquals(401, addServerResponse.getStatus());
        assertEquals("Admin rights required", addServerResponse.getBody());

        HttpResponse<String> loginSuccess = Unirest.post(createURLWithPort("/authentication/login"))
                .header("Accept", "*/*")
                .header("Origin", "http://localhost")
                .multiPartContent()
                .field("username", "admin")
                .field("password", adminPassword)
                .asString();

        String location = loginSuccess.getHeaders().get("Location").get(0).toString();
        location = location.substring(location.length() - 7);

        assertEquals("success", location);

        addServerResponse = Unirest.post(createURLWithPort("/admin/api/cd-server/register"))
                .header("Content-Type", "application/json")
                .body(server.toString())
                .asString();

        assertEquals(400, addServerResponse.getStatus());
        assertEquals("Address or token are missing", addServerResponse.getBody());

        server.put("address", cdServerAddress);

        addServerResponse = Unirest.post(createURLWithPort("/admin/api/cd-server/register"))
                .header("Content-Type", "application/json")
                .body(server.toString())
                .asString();

        assertEquals(400, addServerResponse.getStatus());
        assertEquals("Address or token are missing", addServerResponse.getBody());


        server.remove("adminToken");
        server.put("adminToken", "invalid-token");

        addServerResponse = Unirest.post(createURLWithPort("/admin/api/cd-server/register"))
                .header("Content-Type", "application/json")
                .body(server.toString())
                .asString();

        assertEquals(400, addServerResponse.getStatus());
        assertEquals("Address or token are invalid", addServerResponse.getBody());
    }


    @AfterAll
    public void cleanUp(){
        Unirest.shutDown();
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
