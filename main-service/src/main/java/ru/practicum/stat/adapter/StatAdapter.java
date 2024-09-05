package ru.practicum.stat.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class StatAdapter {
    private static final String EVENT_URI_TEMPLATE = "/events/";
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
        EndpointHit endpointHit = EndpointHit.builder()
                .app(appName)
                .ip(request.getRemoteHost())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        statClient.post(endpointHit);
        log.info("Отправлена статистика: {}", endpointHit);
    }

    public void setStatsForEvent(List<EventFullDto> events) {
        try {
            LocalDateTime time = events.stream().sorted(Comparator.comparing(EventFullDto::getPublishedOn))
                    .map(EventFullDto::getPublishedOn)
                    .findFirst().orElseThrow(() -> new RuntimeException("Нет запросов на участие"));
            List<String> uris = events.stream().map(event -> EVENT_URI_TEMPLATE + event.getId()).toList();
            Map<Long, ViewStats> stats = getStats(new URLParameter(time, LocalDateTime.now(), uris, UNIQUE_VIEWS));
            EventStatConverter.convertToEventWithStatistic(events, stats);
        } catch (RuntimeException e) {
            log.info("Нет запросов на участие в events");
        }
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