package dsd.codebenders.tournament_app.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class HTTPRequestsSender {

    private static String token;
    @Value("${code-defenders.token:BZN76hXNoeVE6phworcmorJFOb1NwOjj}")
    private void setToken(String token) {
        HTTPRequestsSender.token = token;
    }

    public static <T> T sendGetRequest(String url, Map<String, String> queryParameters, Class<T> returnType) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        for(String s: queryParameters.keySet()) {
            builder.queryParam(s, queryParameters.get(s));
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        ResponseEntity<T> result = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, returnType);
        return result.getBody();
    }

    public static <T> T sendPostRequest(String url, Object body, Class<T> returnType) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Object> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(body), headers);
        ResponseEntity<T> result = restTemplate.postForEntity(url, entity, returnType);
        return result.getBody();
    }

}
