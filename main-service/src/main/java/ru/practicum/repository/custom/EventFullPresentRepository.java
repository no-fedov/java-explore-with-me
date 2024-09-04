package ru.practicum.repository.custom;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.URLParameterEventAdmin;

import java.util.List;

public interface EventFullPresentRepository {
    List<EventFullDto> getEvents(URLParameterEventAdmin parameters);
}
