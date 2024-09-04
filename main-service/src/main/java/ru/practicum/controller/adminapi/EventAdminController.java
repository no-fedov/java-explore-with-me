package ru.practicum.controller.adminapi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.controller.PageConstructor;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.URLParameterEventAdmin;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.service.EventAdminService;
import ru.practicum.stat.adapter.StatAdapter;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/events")
@Validated
@Slf4j
public class EventAdminController {
    private static final String timeFormat = "yyyy-MM-dd HH:mm:ss";

    private final EventAdminService eventAdminService;
    private final StatAdapter statAdapter;

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(defaultValue = "") List<Long> users,
                                        @RequestParam(defaultValue = "") List<String> states,
                                        @RequestParam(defaultValue = "") List<Long> categories,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = timeFormat) LocalDateTime rangeStart,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = timeFormat) LocalDateTime rangeEnd,
                                        @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                        @Positive @RequestParam(defaultValue = "10") Integer size) {
        URLParameterEventAdmin parameters = URLParameterEventAdmin.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .page(PageConstructor.getPage(from, size))
                .build();
        parameters.checkValid();
        log.info("GET /admin/events with parameters = {}", parameters);
        List<EventFullDto> events = eventAdminService.getEvents(parameters);
        statAdapter.setStatsForEvent(events);
        return events;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest eventDto) {
        log.info("PATCH /admin/events/{eventId} eventId = {} body = {}", eventId, eventDto);
        EventFullDto eventFullDto = eventAdminService.updateEvent(eventId, eventDto);
        statAdapter.setStatsForEvent(List.of(eventFullDto));
        return eventFullDto;
    }
}