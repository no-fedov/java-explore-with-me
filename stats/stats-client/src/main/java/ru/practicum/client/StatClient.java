package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.URLParameter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatClient {
    private static final String HIT = "/hit";
    private static final String STATS = "/stats";
    private static final String param = "?start={start}&end={end}&unique={unique}";

    private final RestTemplate rest;

    @Autowired
    public StatClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.rest = builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    public ResponseEntity<Object> post(EndpointHit body) {
        return makeAndSendRequest(HttpMethod.POST, HIT, null, body);
    }

    public ResponseEntity<Object> get(URLParameter urlParameter) {
        Map<String, Object> parameters = buildParametersForUri(urlParameter);
        return makeAndSendRequest(HttpMethod.GET, STATS + pathBuilderForUris(parameters), parameters, null);
    }


    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method,
                                                          String path,
                                                          @Nullable Map<String, Object> parameters,
                                                          @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> statsServerResponse;
        try {
            if (parameters != null) {
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(statsServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
    }

    public static Map<String, Object> buildParametersForUri(URLParameter urlParameter) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", urlParameter.getStart().format(DateTimeFormatter.
                ofPattern("yyyy-MM-dd HH:mm:ss")));
        parameters.put("end", urlParameter.getEnd().format(DateTimeFormatter.
                ofPattern("yyyy-MM-dd HH:mm:ss")));
        if (urlParameter.getUris() != null) {
            parameters.put("uris", String.join(",", urlParameter.getUris()));
        }
        parameters.put("unique", urlParameter.getUnique());
        return parameters;
    }

    public static String pathBuilderForUris(Map<String, Object> parameters) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("?");
        for (String key : parameters.keySet()) {
            stringBuilder.append(key).append("={" + key + "}").append("&");
        }
        String path = stringBuilder.toString();
        return path.substring(0, path.length() - 1);
    }

    public static void main(String[] args) {
        StatClient statClient = new StatClient("http://localhost:9090", new RestTemplateBuilder());
//        EndpointHit endpointHit = EndpointHit.builder()
//                .app("dsfsdfsdf")
//                .ip("sdsdfsdf")
//                .timestamp(LocalDateTime.now())
//                .uri("dfsg>ddsgsdg")
//                .build();
        URLParameter unique = URLParameter.builder()
                .start(LocalDateTime.parse("2024-08-13 08:41:45", DateTimeFormatter.
                        ofPattern("yyyy-MM-dd HH:mm:ss")))
                .end(LocalDateTime.now())
                .uris(List.of("/events/2", "/events/1"))
                .unique(Boolean.FALSE).build();

        System.out.println(unique.getStart().format(DateTimeFormatter.
                ofPattern("yyyy-MM-dd HH:mm:ss")));

        Map<String, Object> map = buildParametersForUri(unique);

        System.out.println(pathBuilderForUris(map));
        System.out.println(statClient.get(unique));
    }
}