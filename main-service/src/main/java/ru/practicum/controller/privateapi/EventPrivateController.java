package ru.practicum.controller.privateapi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.RequestDto;
import ru.practicum.dto.event.*;
import ru.practicum.service.EventService;
import ru.practicum.service.RequestService;

import java.util.List;

import static ru.practicum.controller.PageConstructor.getPage;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventPrivateController {
    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventShortDto postEvent(@PositiveOrZero @PathVariable Long userId,
                                   @Valid @RequestBody NewEventDto eventDto) {
        eventDto.checkValid();
        log.info("POST /users/{userId}/events userId = {} , body = {}", userId, eventDto);
        return eventService.addEvent(userId, eventDto);
    }

    @PatchMapping("/{eventId}")
    public EventShortDto patchEvent(@PositiveOrZero @PathVariable Long userId,
                                    @PositiveOrZero @PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest eventUpdateDto) {
        eventUpdateDto.setInitiator(userId);
        eventUpdateDto.setId(eventId);
        log.info("PATCH /users/{userId}/events userId = {} , body = {}", userId, eventUpdateDto);
        return eventService.updateEvent(eventUpdateDto);
    }

    /**
     * Получение событий, добавленных текущим пользователем
     */
    @GetMapping
    public List<EventShortDto> getEvents(@Positive @PathVariable Long userId,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size) {
        Pageable page = getPage(from, size);
        log.info("GET /users/{userId}/events userId = {}", userId);
        return eventService.getEvents(userId, page);
    }

    /**
     * Получение полной информации о событии добавленном текущим пользователем
     */
    @GetMapping("/{eventId}")
    public EventShortDto getEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("GET /users/{userId}/events/{eventId} userId = {} , eventId = {}", userId, eventId);
        return eventService.getEvent(userId, eventId);
    }

    /**
     * Получение информации о запросах на участие в событии текущего пользователя
     */
    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequestsByEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("GET /users/{userId}/events/{eventId}/requests userId = {} eventId = {}", userId, eventId);
        return requestService.getRequestByEvent(userId, eventId);
    }

    /**
     * Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
     */
    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return requestService.updateStatusRequest(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
