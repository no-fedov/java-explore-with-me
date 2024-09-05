package ru.practicum.service;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.URLParameterEventPublic;

import java.util.List;

public interface EventPublicService {
    List<EventFullDto> findEvents(URLParameterEventPublic parameters);

    EventFullDto findEvent(Long id);
}
