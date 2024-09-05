package ru.practicum.repository.custom;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.URLParameterEventAdmin;
import ru.practicum.dto.event.URLParameterEventPublic;

import java.util.List;

public interface EventFullPresentRepository {
    List<EventFullDto> getEventsForAdmin(URLParameterEventAdmin parameters);

    List<EventFullDto> getEventsForPublic(URLParameterEventPublic parameters);

    EventFullDto getEvent(Long eventId);
}
