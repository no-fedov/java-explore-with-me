package ru.practicum.controller.publiс_api;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.controller.PageConstructor;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.URLParameterEventPublic;
import ru.practicum.service.EventPublicService;

import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class EventPublicController {
    private final EventPublicService eventPublicService;
    private static final DateTimeFormatter timeFormat = DateTimeFormatter
            .ofPattern(URLDecoder.decode("yyyy-MM-dd HH:mm:ss", UTF_8));

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable ,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size) {
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
        return eventPublicService.findEvents(parameters);
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PositiveOrZero @PathVariable Long id) {
        return eventPublicService.findEvent(id);
    }
}
