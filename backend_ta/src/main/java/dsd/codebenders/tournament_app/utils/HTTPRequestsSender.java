package dsd.codebenders.tournament_app.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

public class HTTPRequestsSender {

    public static <T> T sendGetRequest(String url, Map<String, String> queryParameters, Class<T> returnType) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        for(String s: queryParameters.keySet()) {
            builder.queryParam(s, queryParameters.get(s));
        }
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<T> result = restTemplate.getForEntity(builder.toUriString(), returnType);
        return result.getBody();
    }

}
