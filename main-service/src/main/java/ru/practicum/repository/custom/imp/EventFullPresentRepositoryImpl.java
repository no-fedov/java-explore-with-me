package ru.practicum.repository.custom.imp;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.LocationDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.URLParameterEventAdmin;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.status.RequestStatus;
import ru.practicum.model.status.StateEvent;
import ru.practicum.repository.custom.EventFullPresentRepository;

import java.util.List;

import static ru.practicum.model.QCategory.category;
import static ru.practicum.model.QEvent.event;
import static ru.practicum.model.QLocation.location;
import static ru.practicum.model.QRequest.request;
import static ru.practicum.model.QUser.user;

@Repository
@RequiredArgsConstructor
public class EventFullPresentRepositoryImpl implements EventFullPresentRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<EventFullDto> getEvents(URLParameterEventAdmin parameters) {
        JPAQuery<EventFullDto> eventsQuery = getEventsQueryTemplate();

        if (!parameters.getStates().isEmpty()) {
            eventsQuery.where(event.state.in(parameters.getStates().stream().map(StateEvent::valueOf).toList()));
        }

        if (!parameters.getUsers().isEmpty()) {
            eventsQuery.where(event.initiator.id.in(parameters.getUsers()));
        }

        if (!parameters.getCategories().isEmpty()) {
            eventsQuery.where(event.category.id.in(parameters.getCategories()));
        }

        if (!parameters.getUsers().isEmpty()) {
            eventsQuery.where(event.initiator.id.in(parameters.getUsers()));
        }

        if (parameters.getRangeStart() != null) {
            eventsQuery.where(event.time.goe(parameters.getRangeStart()));
        }

        if (parameters.getRangeEnd() != null) {
            eventsQuery.where(event.time.loe(parameters.getRangeEnd()));
        }

        eventsQuery.offset(parameters.getPage().getOffset())
                .limit(parameters.getPage().getPageSize());

        return eventsQuery.fetch();
    }

    private JPAQuery<EventFullDto> getEventsQueryTemplate() {
        JPAQuery<EventFullDto> eventsQuery = queryFactory.select(Projections.constructor(EventFullDto.class,
                                event.id,
                                Projections.constructor(UserShortDto.class,
                                        user.id,
                                        user.name),
                                Projections.constructor(CategoryDto.class,
                                        category.id,
                                        category.name),
                                Projections.constructor(LocationDto.class,
                                        location.locationId.lat,
                                        location.locationId.lon),
                                event.title,
                                event.annotation,
                                event.description,
                                event.participantLimit,
                                request.count(),
                                event.paid,
                                event.requestModeration,
                                event.state,
                                event.createdOn,
                                event.time,
                                event.publishedOn,
                                Expressions.constant(0L) // искуственно устанавливаю количество просмотров
                        )
                )
                .from(event)
                .leftJoin(event.initiator, user)
                .leftJoin(event.category, category)
                .leftJoin(event.location, location)
                .leftJoin(request).on(request.event.id.eq(event.id)
                        .and(request.status.eq(RequestStatus.CONFIRMED)))
                .groupBy(event.id, user.id, category.id, location.locationId.lat, location.locationId.lon);
        return eventsQuery;
    }
}
