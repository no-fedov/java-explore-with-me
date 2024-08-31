package ru.practicum.controller.admin_api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.controller.PageConstructor;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.URLParameterEventAdmin;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.service.EventAdminService;

import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/events")
@Slf4j
public class EventAdminController {
    private static final DateTimeFormatter timeFormat = DateTimeFormatter
            .ofPattern(URLDecoder.decode("yyyy-MM-dd HH:mm:ss", UTF_8));

    private final EventAdminService eventAdminService;

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<String> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) String rangeStart,
                                        @RequestParam(required = false) String rangeEnd,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {
        LocalDateTime startTime = null;
        if (rangeStart != null) {
            startTime = LocalDateTime.parse(rangeStart, timeFormat);
        }
        LocalDateTime endTime = null;
        if (rangeEnd != null) {
            endTime = LocalDateTime.parse(rangeEnd, timeFormat);
        }
        URLParameterEventAdmin parameters = URLParameterEventAdmin.builder()
                .users(users != null ? users : List.of())
                .states(states != null ? states : List.of())
                .categories(categories != null ? categories : List.of())
                .rangeStart(startTime)
                .rangeEnd(endTime)
                .page(PageConstructor.getPage(from, size))
                .build();
        parameters.checkValid();
        return eventAdminService.getEvents(parameters);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest eventDto) {
        log.info("PATCH /admin/events/{eventId} eventId = {} body = {}", eventId, eventDto);
        return eventAdminService.updateEvent(eventId, eventDto);
    }
}
