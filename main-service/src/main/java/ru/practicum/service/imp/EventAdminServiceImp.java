package ru.practicum.service.imp;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.URLParameterEventAdmin;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.mapper.EventMapper;
import ru.practicum.exception.EventActionException;
import ru.practicum.exception.NoValidParameter;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.*;
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

    @Transactional
    @Override
    public List<EventFullDto> getEvents(URLParameterEventAdmin parameters) {
        return eventRepository.getEventsForAdmin(parameters);
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

        if (eventDto.getStateAction() == UpdateEventAdminRequest.StateActionAdmin.PUBLISH_EVENT
                && event.getState() != StateEvent.PENDING) {
            throw new EventActionException("Событие можно публиковать, только если оно в состоянии ожидания публикации");
        }

        if (eventDto.getStateAction() == UpdateEventAdminRequest.StateActionAdmin.REJECT_EVENT
                && event.getState() == StateEvent.PUBLISHED) {
            throw new EventActionException("Событие можно отклонить, только если оно еще не опубликовано");
        }

        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction() == UpdateEventAdminRequest.StateActionAdmin.PUBLISH_EVENT) {
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
        return eventFullDto;
    }
}