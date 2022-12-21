package dsd.codebenders.tournament_app.testUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class TestRunner {
    @Autowired
    private MockMvc mockMvc;

    public ResultActions testGet(String url) throws Exception {
        return testGet(url, Collections.emptyMap());
    }

    public ResultActions testGet(String url, Map<String, String> parameters) throws Exception {
        return testGet(url, parameters, List.of(status().isOk()));
    }

    public ResultActions testGet(String url, Map<String, String> parameters, List<ResultMatcher> resultMatchers) throws Exception {
        MockHttpServletRequestBuilder requestBuilder;
        requestBuilder = get(url).contentType(MediaType.APPLICATION_JSON);
        parameters.forEach(requestBuilder::param);
        return runTest(requestBuilder, resultMatchers);
    }

    public ResultActions testPost(String url, Object body) throws Exception {
        return testPost(url, body, List.of(status().isOk()));
    }

    public ResultActions testPost(String url, Object body, List<ResultMatcher> resultMatchers) throws Exception {
        String jsonString;
        if (body instanceof String) {
            jsonString = (String) body;
        } else {
            jsonString = new ObjectMapper().registerModule(new JSONPatches()).writeValueAsString(body);
        }
        MockHttpServletRequestBuilder requestBuilder;
        requestBuilder = post(url);
        requestBuilder = requestBuilder.content(jsonString);
        return runTest(requestBuilder, resultMatchers);
    }

    public ResultActions runTest(MockHttpServletRequestBuilder request, List<ResultMatcher> resultMatchers) throws Exception {
        return mockMvc.perform(request.contentType(MediaType.APPLICATION_JSON)).andDo(print()).andExpectAll(resultMatchers.toArray(new ResultMatcher[0]));
    }
}
