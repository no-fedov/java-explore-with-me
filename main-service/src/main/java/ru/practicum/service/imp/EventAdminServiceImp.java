package ru.practicum.service.imp;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.LocationDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.StateActionAdmin;
import ru.practicum.dto.event.URLParameterEventAdmin;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.mapper.EventMapper;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.exception.EventActionException;
import ru.practicum.exception.NoValidParameter;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.*;
import ru.practicum.model.status.RequestStatus;
import ru.practicum.model.status.StateEvent;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.service.EventAdminService;
import ru.practicum.stat.adapter.StatAdapter;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.dto.mapper.EventMapper.convertToUpdatedEventDtoFromEventAndUpdateEventAdmin;

@Service
@RequiredArgsConstructor
public class EventAdminServiceImp implements EventAdminService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatAdapter statAdapter;
    private final JPAQueryFactory queryFactory;
    private final QEvent event = QEvent.event;
    private final QLocation location = QLocation.location;
    private final QUser user = QUser.user;
    private final QCategory category = QCategory.category;
    private final QRequest request = QRequest.request;

    @Transactional
    @Override
    public List<EventFullDto> getEvents(URLParameterEventAdmin parameters) {
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
        List<EventFullDto> events = eventsQuery.fetch();
        statAdapter.setStatsForEvent(events);
        return events;
    }

    @Transactional
    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest eventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("нет события с  id = " + eventId + ";"));
        LocalDateTime startTime = eventDto.getEventDate();

        if (startTime != null
                && startTime.isBefore(LocalDateTime.now().minusHours(1))) {
            throw new NoValidParameter("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
        }

        if (eventDto.getStateAction() == StateActionAdmin.PUBLISH_EVENT
                && event.getState() != StateEvent.PENDING) {
            throw new EventActionException("Событие можно публиковать, только если оно в состоянии ожидания публикации");
        }

        if (eventDto.getStateAction() == StateActionAdmin.REJECT_EVENT
                && event.getState() == StateEvent.PUBLISHED) {
            throw new EventActionException("Событие можно отклонить, только если оно еще не опубликовано");
        }

        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction() == StateActionAdmin.PUBLISH_EVENT) {
                event.setState(StateEvent.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else {
                event.setState(StateEvent.CANCELED);
            }
        }
        convertToUpdatedEventDtoFromEventAndUpdateEventAdmin(event, eventDto);
        eventRepository.save(event);
        EventFullDto eventFullDto = EventMapper.convertToEventFullDtoFromEvent(event);

        Long confirmedRequestCounter = requestRepository.getCountParticipants(eventId);
        eventFullDto.setConfirmedRequests(confirmedRequestCounter);
        statAdapter.setStatsForEvent(List.of(eventFullDto));
        return eventFullDto;
    }
}