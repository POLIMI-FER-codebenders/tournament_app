package dsd.codebenders.tournament_app.utils;

import dsd.codebenders.tournament_app.errors.HTTPResponseException;
import org.springframework.http.*;
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

    public static <T> T sendPostRequest(String url, Object body, Class<T> returnType) throws HTTPResponseException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        ResponseEntity<T> result = restTemplate.postForEntity(url, entity, returnType);
        //TODO: improve HTTPResponseException
        if(result.getStatusCode() != HttpStatus.OK) {
            throw new HTTPResponseException(result.getStatusCodeValue());
        }
        return result.getBody();
    }

}
