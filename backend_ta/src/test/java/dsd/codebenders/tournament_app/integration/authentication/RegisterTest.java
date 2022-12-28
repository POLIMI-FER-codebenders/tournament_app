package dsd.codebenders.tournament_app.integration.authentication;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import dsd.codebenders.tournament_app.dao.PlayerRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import org.springframework.transaction.annotation.Transactional;

@TestPropertySource(locations = "classpath:application-integration.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegisterTest {
    @LocalServerPort
    private int port;
    private String[] userValid = {"hrvoje459", "hrvoje@hrvoje.hr", "testTestT1"};
    private String[] userUsernameTaken = {"hrvoje459", "hrvoje459@hrvoje.hr", "testTestT1"};
    private String[] userEmailTaken = {"hrvoje367", "hrvoje@hrvoje.hr", "testTestT1"};
    private String[] userInvalidUsername = {"459hrvoje", "hrvoje@hrvoje.hr", "testTestT1"};
    private String[] userInvalidEmail = {"hrvoje459", "@hrvoje.hrvoje.hr", "testTestT1"};
    private String[] userInvalidPassword= {"hrvoje367", "hrvoje@hrvoje.hr", "testtestt1"};


    @Autowired
    private PlayerRepository playerRepository;

    /*@Autowired
    Flyway flyway;

    @Autowired
    FlywayMigrationStrategy flywayMigrationStrategy;

    @BeforeAll
    public void cleanUp(){
        flyway.clean();
        flywayMigrationStrategy.migrate(flyway);
    }*/
    @Test
    @Order(1)
    void registerSuccessTest()  {

        //playerRepository.delete(playerRepository.findByUsername("hrvoje459"));

        //System.out.println(playerRepository.findAll());

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode user = mapper.createObjectNode();

        user.put("username", userValid[0]);
        user.put("email", userValid[1]);
        user.put("password", userValid[2]);

        //String niceJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);

        HttpResponse<String> registrationSuccess = Unirest.post(createURLWithPort("/authentication/register"))
                .header("Content-Type", "application/json")
                .body(user.toString())
                .asString();

        System.out.println("=" + registrationSuccess.getBody() + "=");
        String expectedRegistrationSuccess = "{\"result\":\"Registered\"}";

        assertEquals(expectedRegistrationSuccess, registrationSuccess.getBody());
    }

    @Test
    @Order(2)
    void usernameTakenTest()  {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode user = mapper.createObjectNode();

        user.put("username", userUsernameTaken[0]);
        user.put("email", userUsernameTaken[1]);
        user.put("password", userUsernameTaken[2]);

        HttpResponse<String> registrationFailure = Unirest.post(createURLWithPort("/authentication/register"))
                .header("Content-Type", "application/json")
                .body(user.toString())
                .asString();

        String expectedRegistrationFailure = "{\"result\":\"Username already taken\"}";

        assertEquals(expectedRegistrationFailure, registrationFailure.getBody());
    }

    @Test
    @Order(3)
    void emailTakenTest()  {


        ObjectMapper mapper = new ObjectMapper();
        ObjectNode user = mapper.createObjectNode();

        user.put("username", userEmailTaken[0]);
        user.put("email", userEmailTaken[1]);
        user.put("password", userEmailTaken[2]);


        HttpResponse<String> registrationFailure = Unirest.post(createURLWithPort("/authentication/register"))
                .header("Content-Type", "application/json")
                .body(user.toString())
                .asString();


        String expectedRegistrationFailure = "{\"result\":\"Email already taken\"}";

        assertEquals(expectedRegistrationFailure, registrationFailure.getBody());

        System.out.println(playerRepository.findAll());

    }

    @Test
    @Order(4)
    void invalidUsernameTest()  {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode user = mapper.createObjectNode();

        user.put("username", userInvalidUsername[0]);
        user.put("email", userInvalidUsername[1]);
        user.put("password", userInvalidUsername[2]);

        HttpResponse<String> registrationFailure = Unirest.post(createURLWithPort("/authentication/register"))
                .header("Content-Type", "application/json")
                .body(user.toString())
                .asString();

        String expectedRegistrationFailure = "Some registration parameters are invalid";

        assertEquals(expectedRegistrationFailure, registrationFailure.getBody());
    }

    @Test
    @Order(5)
    void invalidEmailTest()  {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode user = mapper.createObjectNode();

        user.put("username", userInvalidEmail[0]);
        user.put("email", userInvalidEmail[1]);
        user.put("password", userInvalidEmail[2]);

        HttpResponse<String> registrationFailure = Unirest.post(createURLWithPort("/authentication/register"))
                .header("Content-Type", "application/json")
                .body(user.toString())
                .asString();

        String expectedRegistrationFailure = "Some registration parameters are invalid";

        assertEquals(expectedRegistrationFailure, registrationFailure.getBody());
    }

    @Test
    @Order(6)
    void invalidPasswordTest()  {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode user = mapper.createObjectNode();

        user.put("username", userInvalidPassword[0]);
        user.put("email", userInvalidPassword[1]);
        user.put("password", userInvalidPassword[2]);

        HttpResponse<String> registrationFailure = Unirest.post(createURLWithPort("/authentication/register"))
                .header("Content-Type", "application/json")
                .body(user.toString())
                .asString();

        String expectedRegistrationFailure = "Some registration parameters are invalid";

        assertEquals(expectedRegistrationFailure, registrationFailure.getBody());
    }

    @AfterAll
    public void cleanUp(){
        playerRepository.delete(playerRepository.findByUsername(userValid[0]));
        Unirest.shutDown();
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
