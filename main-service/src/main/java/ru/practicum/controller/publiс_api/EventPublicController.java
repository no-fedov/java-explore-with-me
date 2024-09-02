package ru.practicum.controller.publiс_api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.config.CustomRepositoryImplementationDetector;
import org.springframework.web.bind.annotation.*;
import ru.practicum.controller.PageConstructor;
import ru.practicum.dto.URLParameter;
import ru.practicum.dto.ViewStats;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.URLParameterEventPublic;
import ru.practicum.service.EventPublicService;
import ru.practicum.stat.adapter.EventStatConverter;
import ru.practicum.stat.adapter.StatAdapter;

import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class EventPublicController {
    private final EventPublicService eventPublicService;
    private final StatAdapter statAdapter;
    private static final DateTimeFormatter timeFormat = DateTimeFormatter
            .ofPattern(URLDecoder.decode("yyyy-MM-dd HH:mm:ss", UTF_8));

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         HttpServletRequest request) {
        URLParameterEventPublic parameters = URLParameterEventPublic.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart == null ? null : LocalDateTime.parse(rangeStart, timeFormat))
                .rangeEnd(rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, timeFormat))
                .onlyAvailable(onlyAvailable)
                .page(PageConstructor.getPage(from, size))
                .build();
        parameters.checkValid();
        List<EventShortDto> events = eventPublicService.findEvents(parameters);
        statAdapter.sendStats(request);
        return events;
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PositiveOrZero @PathVariable Long id, HttpServletRequest request) {
        EventFullDto event = eventPublicService.findEvent(id);
        statAdapter.sendStats(request);
        statAdapter.setStatsForEvent(List.of(event));
        return event;
    }
}
