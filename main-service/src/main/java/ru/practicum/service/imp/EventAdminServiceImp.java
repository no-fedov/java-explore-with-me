package ru.practicum.service.imp;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.LocationDto;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.URLParameterEventAdmin;
import ru.practicum.model.QEvent;
import ru.practicum.model.QLocation;
import ru.practicum.model.status.StateEvent;
import ru.practicum.service.EventAdminService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventAdminServiceImp implements EventAdminService {
    private final JPAQueryFactory queryFactory;
    private final QEvent event = QEvent.event;
    private final QLocation location = QLocation.location;

    @Override
    public List<EventDto> getEvents(URLParameterEventAdmin parameters) {
        JPAQuery<EventDto> eventsQuery = queryFactory.select(Projections.constructor(EventDto.class,
                        event.id,
                        event.initiator.id,
                        event.category.id,
                        event.title,
                        event.annotation,
                        event.description,
                        event.participantLimit,
                        event.time,
                        Projections.constructor(LocationDto.class, // Проекция на LocationDto
                                location.locationId.lat,
                                location.locationId.lon),
                        event.paid,
                        event.requestModeration,
                        event.state,
                        event.createdOn)
                )
                .from(event).leftJoin(event.location, location)
                .where(event.initiator.id.in(parameters.getUsers()))
                .where(event.state.in(parameters.getStates().stream().map(e -> StateEvent.valueOf(e)).toList()))
                .where(event.category.id.in(parameters.getCategories()))
                .where(event.time.goe(parameters.getRangeStart()))
                .where(event.time.loe(parameters.getRangeEnd()))
                .offset(parameters.getPage().getOffset())
                .limit(parameters.getPage().getPageSize());
        return eventsQuery.fetch();
    }
}