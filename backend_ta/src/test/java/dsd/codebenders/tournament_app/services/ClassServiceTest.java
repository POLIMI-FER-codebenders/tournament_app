package dsd.codebenders.tournament_app.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import dsd.codebenders.tournament_app.dao.GameClassRepository;
import dsd.codebenders.tournament_app.entities.CDGameClass;
import dsd.codebenders.tournament_app.entities.GameClass;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.entities.Server;
import dsd.codebenders.tournament_app.errors.CDServerUnreachableException;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureMockMvc
class ClassServiceTest {
    @Autowired
    private ClassService classService;

    @MockBean
    private GameClassRepository gameClassRepository;

    @MockBean
    private ServerService serverService;

    @MockBean
    private CDGameClassService cdGameClassService;

    @Test
    void testUploadClass() throws CDServerUnreachableException, IOException {
        // Setup test data
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getBytes()).thenReturn("public class Test {}".getBytes());
        Player author = new Player();

        // Setup mock behavior
        Mockito.when(file.getOriginalFilename()).thenReturn("test.java");
        Mockito.when(gameClassRepository.existsByFilename("test.java")).thenReturn(false);
        Mockito.doAnswer(invocation -> {
            GameClass gameClass = invocation.getArgument(0);
            gameClass.setData("public class Test {}".getBytes());
            return null;
        }).when(gameClassRepository).save(Mockito.any(GameClass.class));
        Server server = new Server();
        Mockito.when(serverService.getCDServer()).thenReturn(server);
        CDGameClass cdGameClass = new CDGameClass();
        try (MockedStatic<HTTPRequestsSender> httpRequestsSenderMockedStatic = Mockito.mockStatic(HTTPRequestsSender.class)){
            httpRequestsSenderMockedStatic.when(() -> HTTPRequestsSender.sendPostRequest(Mockito.eq(server), Mockito.eq("/admin/api/class/upload"), Mockito.any(CDClassUploadRequest.class), Mockito.eq(CDGameClass.class))).thenReturn(cdGameClass);

            // Invoke method under test
            GameClass result = classService.uploadClass(file, author);

            // Verify results
            assertNotNull(result);
            assertArrayEquals("public class Test {}".getBytes(), result.getData());
            Mockito.verify(gameClassRepository).save(Mockito.any(GameClass.class));
            Mockito.verify(cdGameClassService).addNewCDGameClass(cdGameClass);
        }
    }

    @Test
    void testGetAllClasses() {
        // Setup test data
        GameClass gameClass1 = new GameClass();
        gameClass1.setFilename("test1.java");
        GameClass gameClass2 = new GameClass();
        gameClass2.setFilename("test2.java");
        List<GameClass> gameClasses = Arrays.asList(gameClass1, gameClass2);

        // Setup mock behavior
        Mockito.when(gameClassRepository.findAll()).thenReturn(gameClasses);

        // Invoke method under test
        List<GameClass> result = classService.getAllClasses();

        // Verify results
        assertEquals(2, result.size());
        assertEquals("test1.java", result.get(0).getFilename());
        assertEquals("test2.java", result.get(1).getFilename());
    }

}
