package ru.practicum.service;

import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.URLParameterEventPublic;

import java.util.List;

public interface EventPublicService {
    List<EventShortDto> findEvents(URLParameterEventPublic parameters);
    EventShortDto findEvent(Long id);
}
