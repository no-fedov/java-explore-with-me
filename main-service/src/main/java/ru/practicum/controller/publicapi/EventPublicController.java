package ru.practicum.controller.publicapi;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.controller.PageConstructor;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.URLParameterEventPublic;
import ru.practicum.service.EventPublicService;
import ru.practicum.stat.adapter.StatAdapter;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventPublicController {
    private static final String timeFormat = "yyyy-MM-dd HH:mm:ss";
    private final EventPublicService eventPublicService;
    private final StatAdapter statAdapter;

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) String text,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) Boolean paid,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = timeFormat) LocalDateTime rangeStart,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = timeFormat) LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                        @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                        @Positive @RequestParam(defaultValue = "10") Integer size,
                                        HttpServletRequest request) {
        URLParameterEventPublic parameters = URLParameterEventPublic.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .page(PageConstructor.getPage(from, size))
                .build();
        parameters.checkValid();
        List<EventFullDto> events = eventPublicService.findEvents(parameters);
        statAdapter.sendStats(request);
        statAdapter.setStatsForEvent(events);
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
