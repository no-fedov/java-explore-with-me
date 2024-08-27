package ru.practicum.service;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ru.practicum.dto.LocationDto;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.URLParameterEventAdmin;
import ru.practicum.model.QEvent;
import ru.practicum.model.QLocation;
import ru.practicum.model.status.StateEvent;

import java.util.List;

public interface EventAdminService {
    List<EventDto> getEvents(URLParameterEventAdmin parameters);
}
