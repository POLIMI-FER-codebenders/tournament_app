package dsd.codebenders.tournament_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dsd.codebenders.tournament_app.entities.Player;
import dsd.codebenders.tournament_app.utils.JSONPatches;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class AuthenticationControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Test
    void register() throws Exception {
        this.mockMvc.perform(post("/authentication/register").contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().registerModule(new JSONPatches()).writeValueAsString(new Player("mario", "mario@gmail.com", "MarioPassword123")))).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(containsString("Hello, World")));
    }
}