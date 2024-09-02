package ru.practicum.service.imp;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.LocationDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.URLParameterEventPublic;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.*;
import ru.practicum.model.status.RequestStatus;
import ru.practicum.model.status.StateEvent;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.EventPublicService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventPublicServiceImp implements EventPublicService {
    private final EventRepository eventRepository;
    private final JPAQueryFactory queryFactory;
    private final QEvent event = QEvent.event;
    private final QLocation location = QLocation.location;
    private final QRequest request = QRequest.request;
    private final QUser user = QUser.user;
    private final QCategory category = QCategory.category;

    public List<EventFullDto> findEvents(URLParameterEventPublic parameters) {
        JPAQuery<EventFullDto> query = getQueryTemplateForFullEventDto();

        if (parameters.getCategories() != null && !parameters.getCategories().isEmpty()) {
            query.where(event.category.id.in(parameters.getCategories()));
        }

        if (parameters.getText() != null && !parameters.getText().trim().isEmpty()) {
            String searchText = "%" + parameters.getText().trim() + "%";
            BooleanBuilder conditionForSearch = new BooleanBuilder();
            conditionForSearch.or(event.description.likeIgnoreCase(searchText))
                    .or(event.title.likeIgnoreCase(searchText))
                    .or(event.annotation.likeIgnoreCase(searchText));
            query.where(conditionForSearch);
        }

        if (parameters.getPaid() != null) {
            query.where(event.paid.eq(parameters.getPaid()));
        }

        if (parameters.getRangeStart() != null) {
            query.where(event.time.goe(parameters.getRangeStart()));
        }

        if (parameters.getRangeEnd() != null) {
            query.where(event.time.loe(parameters.getRangeEnd()));
        }

        if (parameters.getOnlyAvailable()) {
            JPAQuery<Long> confirmedRequestCountSubquery = queryFactory
                    .select(request.count())
                    .from(request)
                    .where(request.event.id.eq(event.id)
                            .and(request.status.eq(RequestStatus.CONFIRMED)));

            query.where(confirmedRequestCountSubquery.lt(event.participantLimit));
        }
        query.offset(parameters.getPage().getOffset())
                .limit(parameters.getPage().getPageSize());
        return query.fetch();
    }

    @Override
    public EventFullDto findEvent(Long id) {
        Optional<Event> currentEvent = eventRepository.findById(id);
        if (currentEvent.isEmpty() || currentEvent.get().getState() != StateEvent.PUBLISHED) {
            throw new NotFoundException("Не найден такой event ли он еще не опубликован");
        }

        JPAQuery<EventFullDto> eventsQuery = getQueryTemplateForFullEventDto()
                .where(event.id.eq(id));

        return eventsQuery.fetchOne();
    }

    private JPAQuery<EventFullDto> getQueryTemplateForFullEventDto() {
        return queryFactory.select(Projections.constructor(EventFullDto.class,
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
                                Expressions.constant(0L)
                        )
                )
                .from(event)
                .leftJoin(event.location, location)
                .leftJoin(event.initiator, user)
                .leftJoin(event.category, category)
                .leftJoin(request).on(request.event.id.eq(event.id)
                        .and(request.status.eq(RequestStatus.CONFIRMED)))
                .groupBy(event.id, user.id, category.id, location.locationId.lat, location.locationId.lon);
    }
}