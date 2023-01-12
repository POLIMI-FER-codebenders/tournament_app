package dsd.codebenders.tournament_app.services;

import dsd.codebenders.tournament_app.dao.ServerRepository;
import dsd.codebenders.tournament_app.entities.CDPlayer;
import dsd.codebenders.tournament_app.entities.Server;
import dsd.codebenders.tournament_app.errors.CDServerAlreadyRegisteredException;
import dsd.codebenders.tournament_app.errors.CDServerNotFoundException;
import dsd.codebenders.tournament_app.errors.CDServerUnreachableException;
import dsd.codebenders.tournament_app.responses.LoadResponse;
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

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureMockMvc
class ServerServiceTest {

    @Autowired
    private ServerService serverService;

    @MockBean
    private ServerRepository serverRepository;

    @Test
    void getCDServer() throws CDServerUnreachableException {
        // mock 4 servers with increasing load
        Server server1 = Mockito.mock(Server.class);
        Server server2 = Mockito.mock(Server.class);
        Server server3 = Mockito.mock(Server.class);
        Server server4 = Mockito.mock(Server.class);

        Mockito.when(serverRepository.findAllActive()).thenReturn(Arrays.asList(server1, server2, server3, server4));

        try (MockedStatic<HTTPRequestsSender> httpRequestsSenderMockedStatic = Mockito.mockStatic(HTTPRequestsSender.class)) {
            LoadResponse loadResponse1 = new LoadResponse();
            loadResponse1.setTotal(1);
            LoadResponse loadResponse2 = new LoadResponse();
            loadResponse2.setTotal(2);
            LoadResponse loadResponse3 = new LoadResponse();
            loadResponse3.setTotal(3);
            LoadResponse loadResponse4 = new LoadResponse();
            loadResponse4.setTotal(4);
            httpRequestsSenderMockedStatic.when(() -> HTTPRequestsSender.sendGetRequest(Mockito.eq(server1), Mockito.eq("/admin/api/load"), Mockito.any())).thenReturn(loadResponse1);
            httpRequestsSenderMockedStatic.when(() -> HTTPRequestsSender.sendGetRequest(Mockito.eq(server2), Mockito.eq("/admin/api/load"), Mockito.any())).thenReturn(loadResponse2);
            httpRequestsSenderMockedStatic.when(() -> HTTPRequestsSender.sendGetRequest(Mockito.eq(server3), Mockito.eq("/admin/api/load"), Mockito.any())).thenReturn(loadResponse3);
            httpRequestsSenderMockedStatic.when(() -> HTTPRequestsSender.sendGetRequest(Mockito.eq(server4), Mockito.eq("/admin/api/load"), Mockito.any())).thenReturn(loadResponse4);

            assertEquals(server1, serverService.getCDServer());

        }

    }

    @Test
    void addNewServer() throws CDServerAlreadyRegisteredException {
        Server server = new Server();
        server.setAddress("http://dummyaddress.com/");

        Mockito.when(serverRepository.findByAddress(Mockito.any(String.class))).thenReturn(null);

        serverService.addServer(server);

        assertEquals(true, server.getIsActive());
        assertEquals("http://dummyaddress.com", server.getAddress());
        Mockito.verify(serverRepository).save(server);
    }

    @Test
    void updateServer() throws CDServerNotFoundException {
        Server server = new Server();
        server.setAddress("http://dummyaddress.com/");
        server.setAdminToken("dummyToken");
        Server storedServer = new Server();
        storedServer.setActive(true);
        storedServer.setAdminToken("dummyToken");

        Mockito.when(serverRepository.findByAddress(Mockito.any(String.class))).thenReturn(storedServer);

        serverService.updateServer(server);

        Mockito.verify(serverRepository).updateToken(Mockito.eq("dummyToken"), Mockito.eq(storedServer));
    }

    @Test
    void deleteServer() throws CDServerNotFoundException {
        Server server = new Server();
        server.setAddress("http://dummyaddress.com/");
        server.setAdminToken("dummyToken");
        Server storedServer = new Server();
        storedServer.setActive(true);
        storedServer.setAdminToken("dummyToken");

        Mockito.when(serverRepository.findByAddress(Mockito.any(String.class))).thenReturn(storedServer);

        serverService.deleteServer(server);

        Mockito.verify(serverRepository).updateAsInactive(Mockito.eq(storedServer));
    }


}
