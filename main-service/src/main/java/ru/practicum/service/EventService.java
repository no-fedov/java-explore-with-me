package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;

import java.util.List;

public interface EventService {
    EventShortDto addEvent(Long userId, NewEventDto eventDto);

    EventShortDto updateEvent(UpdateEventUserRequest eventUpdateDto);

    List<EventShortDto> getEvents(Long userId, Pageable page);

    EventShortDto getEvent(Long userId, Long eventId);
 }