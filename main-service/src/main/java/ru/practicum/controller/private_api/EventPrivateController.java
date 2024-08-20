package ru.practicum.controller.private_api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.EventUpdateDto;
import ru.practicum.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.controller.PageConstructor.getPage;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto postEvent(@Positive @PathVariable Long userId,
                              @Valid @RequestBody EventDto eventDto) {
        eventDto.setInitiator(userId);
        eventDto.setCreatedOn(LocalDateTime.now());
        return eventService.addEvent(eventDto);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventDto patchEvent(@Positive @PathVariable Long userId,
                               @Positive @PathVariable Long eventId,
                               @Valid @RequestBody EventUpdateDto eventUpdateDto) {
        eventUpdateDto.setInitiator(userId);
        eventUpdateDto.setId(eventId);
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
        return eventService.getEvents(userId, page);
    }

    /**
     * Получение полной информации о событии добавленном текущим пользователем
     */
    @GetMapping("/{userId}/events/{eventId}")
    public EventDto getEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getEvent(userId, eventId);
    }
}
