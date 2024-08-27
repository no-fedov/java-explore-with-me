package ru.practicum.service.imp;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.LocationDto;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.URLParameterEventPublic;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.QEvent;
import ru.practicum.model.QLocation;
import ru.practicum.model.QRequest;
import ru.practicum.model.status.RequestStatus;
import ru.practicum.model.status.StateEvent;
import ru.practicum.service.EventPublicService;
import ru.practicum.service.EventService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventPublicServiceImp implements EventPublicService {
    private final EventService eventService;
    private final JPAQueryFactory queryFactory;
    private final QEvent event = QEvent.event;
    private final QLocation location = QLocation.location;
    private final QRequest request = QRequest.request;

    public List<EventDto> findEvents(URLParameterEventPublic parameters) {
        JPAQuery<EventDto> query = queryFactory.select(Projections.constructor(EventDto.class,
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
                        event.createdOn))
                .from(event).leftJoin(event.location, location);


        if (parameters.getCategories() != null && !parameters.getCategories().isEmpty()) {
            query.where(event.category.id.in(parameters.getCategories()));
        }

        if (parameters.getText() != null && !parameters.getText().trim().isEmpty()) {
            query.where(event.description.like("*" + parameters.getText() + "*")
                    .or(event.title.like("*" + parameters.getText() + "*")));
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
                            .and(request.status.eq(RequestStatus.ACCEPTED)));

            query.where(confirmedRequestCountSubquery.lt(event.participantLimit));
        }
        query.offset(parameters.getPage().getOffset())
                .limit(parameters.getPage().getPageSize());
        return query.fetch();
    }


    //скорее всего здесь другой метод надо, так как доп требования
    public EventDto findEvent(Long id) {
        EventDto event = eventService.findEvent(id);
        if (event.getState() != StateEvent.PUBLISHED) {
            throw new NotFoundException("Не найден такой event ли он еще не опубликован");
        }
        return event;
    }
}