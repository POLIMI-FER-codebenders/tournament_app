package dsd.codebenders.tournament_app.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dsd.codebenders.tournament_app.entities.Server;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Component
public class HTTPRequestsSender {

    public static <T> T sendGetRequest(Server server, String api, Class<T> returnType) {
        return sendGetRequest(server, api, new HashMap<>(), returnType);
    }

    public static <T> T sendGetRequest(Server server, String api, Map<String, String> queryParameters, Class<T> returnType) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(server.getAddress() + api);
        for(String s: queryParameters.keySet()) {
            builder.queryParam(s, queryParameters.get(s));
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + server.getAdminToken());
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        ResponseEntity<T> result = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, returnType);
        return result.getBody();
    }

    public static <T> T sendPostRequest(Server server, String api, Object body, Class<T> returnType) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + server.getAdminToken());
        HttpEntity<Object> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(body), headers);
        ResponseEntity<T> result = restTemplate.postForEntity(server.getAddress() + api, entity, returnType);
        return result.getBody();
    }

}
