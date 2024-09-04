package ru.practicum.stat.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.client.StatClient;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.URLParameter;
import ru.practicum.dto.ViewStats;
import ru.practicum.dto.event.EventFullDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StatAdapter {
    private static final String EVENT_URI_TEMPLATE = "/events/";
    private static final LocalDateTime TIME_FROM = LocalDateTime.of(1000, 1, 1, 1, 1);
    private static final Boolean UNIQUE_VIEWS = Boolean.TRUE;

    private final String appName;
    private final StatClient statClient;
    private final ObjectMapper mapper;

    @Autowired
    public StatAdapter(@Value("${app.name}") String appName, StatClient statClient, ObjectMapper mapper) {
        this.appName = appName;
        this.statClient = statClient;
        this.mapper = mapper;
    }

    public void sendStats(HttpServletRequest request) {
        statClient.post(EndpointHit.builder()
                .app(appName)
                .ip(request.getRemoteHost())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());

        System.out.println("ИМЯ ПРИЛОЖЕНИЯ: " + appName);
        System.out.println("IP : " + request.getRemoteHost());
        System.out.println("URI: " + request.getRequestURI());
        System.out.println(request);
    }

    public void setStatsForEvent(List<EventFullDto> events) {
        List<String> uris = events.stream().map(event -> EVENT_URI_TEMPLATE + event.getId()).toList();
        Map<Long, ViewStats> stats = getStats(new URLParameter(TIME_FROM, LocalDateTime.now(), uris, UNIQUE_VIEWS));
        EventStatConverter.convertToEventWithStatistic(events, stats);
    }

    private Map<Long, ViewStats> getStats(URLParameter parameter) {
        ResponseEntity<Object> response = statClient.get(parameter);
        return parseResponse(response);
    }

    private Map<Long, ViewStats> parseResponse(ResponseEntity<?> response) {
        if (response.getStatusCode().isError()) {
            throw new RuntimeException("Не удалось загрузить статистику");
        }
        Map<Long, ViewStats> statsMap = new HashMap<>();
        try {
            List<ViewStats> responseBody = mapper.convertValue(response.getBody(),
                    new TypeReference<>() {
                    });
            if (responseBody != null) {
                for (ViewStats stats : responseBody) {
                    String uri = stats.getUri();
                    String[] parts = uri.split("/");
                    Long eventId = Long.valueOf(parts[parts.length - 1]);
                    statsMap.put(eventId, stats);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при парсинге ответа", e);
        }
        return statsMap;
    }
}