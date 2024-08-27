package ru.practicum.service;

import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.URLParameterEventPublic;

import java.util.List;

public interface EventPublicService {
    List<EventDto> findEvents(URLParameterEventPublic parameters);
    EventDto findEvent(Long id);
}
