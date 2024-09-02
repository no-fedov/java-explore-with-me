package ru.practicum.stat.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class StatAdapter {
    private final static String APP_NAME = "ewm-main-service";
    private final static String EVENT_URI_TEMPLATE = "/events/";
    private final static LocalDateTime TIME_FROM = LocalDateTime.of(1000,1,1,1,1);
    private final static Boolean UNIQUE_VIEWS = Boolean.TRUE;

    private final StatClient statClient;
    private ObjectMapper mapper;

    public void sendStats(HttpServletRequest request) {
        statClient.post(EndpointHit.builder()
                .app("ewm-main-service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build());
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