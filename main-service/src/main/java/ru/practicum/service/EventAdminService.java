package ru.practicum.service;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.URLParameterEventAdmin;
import ru.practicum.dto.event.UpdateEventAdminRequest;

import java.util.List;

public interface EventAdminService {
    List<EventFullDto> getEvents(URLParameterEventAdmin parameters);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest eventDto);
}
