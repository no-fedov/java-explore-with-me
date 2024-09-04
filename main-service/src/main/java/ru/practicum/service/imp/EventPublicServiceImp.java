package ru.practicum.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.URLParameterEventPublic;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.status.StateEvent;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.EventPublicService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventPublicServiceImp implements EventPublicService {
    private final EventRepository eventRepository;

    public List<EventFullDto> findEvents(URLParameterEventPublic parameters) {
        return eventRepository.getEventsForPublic(parameters);
    }

    @Override
    public EventFullDto findEvent(Long id) {
        Optional<Event> currentEvent = eventRepository.findById(id);
        if (currentEvent.isEmpty() || currentEvent.get().getState() != StateEvent.PUBLISHED) {
            throw new NotFoundException("Не найден такой event ли он еще не опубликован");
        }

        return eventRepository.getEvent(id);
    }
}