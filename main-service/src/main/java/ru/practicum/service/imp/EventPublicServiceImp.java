package ru.practicum.service.imp;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.LocationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.URLParameterEventPublic;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.*;
import ru.practicum.model.status.RequestStatus;
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
    private final QUser user = QUser.user;
    private final QCategory category = QCategory.category;

    public List<EventShortDto> findEvents(URLParameterEventPublic parameters) {
        JPAQuery<EventShortDto> query = queryFactory.select(Projections.constructor(EventShortDto.class,
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
                        event.time,
                        event.paid,
                        event.requestModeration,
                        event.state,
                        event.createdOn))
                .from(event).leftJoin(event.location, location)
                .leftJoin(event.initiator, user)
                .leftJoin(event.category, category);

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
                            .and(request.status.eq(RequestStatus.CONFIRMED)));

            query.where(confirmedRequestCountSubquery.lt(event.participantLimit));
        }
        query.offset(parameters.getPage().getOffset())
                .limit(parameters.getPage().getPageSize());
        return query.fetch();
    }

    @Override
    public EventShortDto findEvent(Long id) {
        return null;
    }

//    //скорее всего здесь другой метод надо, так как доп требования
//    public EventShortDto findEvent(Long id) {
//        EventShortDto event = eventService.findEvent(id);
//        if (event.getState() != StateEvent.PUBLISHED) {
//            throw new NotFoundException("Не найден такой event ли он еще не опубликован");
//        }
//        return event;
//    }
}