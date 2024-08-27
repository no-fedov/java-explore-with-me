package ru.practicum.controller.private_api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.EventUpdateDto;
import ru.practicum.model.status.StateEvent;
import ru.practicum.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.controller.PageConstructor.getPage;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto postEvent(@PositiveOrZero @PathVariable Long userId,
                              @Valid @RequestBody EventDto eventDto) {
        eventDto.setInitiator(userId);
        eventDto.setCreatedOn(LocalDateTime.now());
        eventDto.setState(StateEvent.PENDING);
        log.info("POST /users/{userId}/events userId = {} , body = {}", userId, eventDto);
        return eventService.addEvent(eventDto);
    }

    @PatchMapping("/{userId}/events/{eventId}")
     public EventDto patchEvent(@PositiveOrZero @PathVariable Long userId,
                               @PositiveOrZero @PathVariable Long eventId,
                               @Valid @RequestBody EventUpdateDto eventUpdateDto) {
        eventUpdateDto.setInitiator(userId);
        eventUpdateDto.setId(eventId);
        log.info("PATCH /users/{userId}/events userId = {} , body = {}", userId, eventUpdateDto);
        return eventService.updateEvent(eventUpdateDto);
    }

    /**
     * Получение событий, добавленных текущим пользователем
     */
    @GetMapping("/{userId}/events")
    public List<EventDto> getEvents(@Positive @PathVariable Long userId,
                                    @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                    @Positive @RequestParam(defaultValue = "10") Integer size) {
        Pageable page = getPage(from, size);
        log.info("GET /users/{userId}/events userId = {}", userId);
        return eventService.getEvents(userId, page);
    }

    /**
     * Получение полной информации о событии добавленном текущим пользователем
     */
    @GetMapping("/{userId}/events/{eventId}")
    public EventDto getEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("POST /users/{userId}/events/{eventId} userId = {} , eventId = {}", userId, eventId);
        return eventService.getEvent(userId, eventId);
    }
}
