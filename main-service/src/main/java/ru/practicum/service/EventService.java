package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.EventUpdateDto;

import java.util.List;

public interface EventService {
    EventDto addEvent(EventDto eventDto);

    EventDto updateEvent(EventUpdateDto eventUpdateDto);

    List<EventDto> getEvents(Long userId, Pageable page);

    EventDto getEvent(Long userId, Long eventId);

    EventDto findEvent(Long eventId);
 }
