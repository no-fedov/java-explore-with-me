package ru.practicum.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.EventUpdateDto;
import ru.practicum.dto.mapper.EventMapper;
import ru.practicum.exception.EventStateException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.status.StateEvent;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.CategoryService;
import ru.practicum.service.EventService;
import ru.practicum.service.LocationService;
import ru.practicum.service.UserService;

import java.util.List;

import static ru.practicum.dto.mapper.EventMapper.*;

@Service
@RequiredArgsConstructor
public class EventPrivateServiceImp implements EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationService locationService;

    @Override
    public EventDto addEvent(EventDto eventDto) {
        UserDto currentUserDto = userService.findUser(eventDto.getInitiator());
        CategoryDto currentCategoryDto = null;
        if (eventDto.getCategory() != null) {
            currentCategoryDto = categoryService.findCategory(eventDto.getCategory());
        }

        locationService.addLocation(eventDto.getLocation());

        Event newEvent = EventMapper.eventFromEventDto(eventDto,
                currentUserDto,
                currentCategoryDto,
                eventDto.getLocation());
        eventRepository.save(newEvent);
        return eventDtoFromEvent(newEvent);
    }

    @Override
    public EventDto updateEvent(EventUpdateDto eventUpdateDto) {
        Event event = findEventById(eventUpdateDto.getId());

        if (eventUpdateDto.getCategory() != null)
            categoryService.findCategory(eventUpdateDto.getCategory());
        //обновить категории забыл я
        if (!event.getInitiator().getId().equals(eventUpdateDto.getInitiator())) {
            throw new NotFoundException("Event with id=" + eventUpdateDto.getId() + "was not found");
        }
        if (event.getState() == StateEvent.PUBLISHED) {
            throw new EventStateException("Event must not be published");
        }

        updatedEvent(event, eventUpdateDto);
        eventRepository.save(event);

        return eventDtoFromEvent(event);
    }

    @Override
    public List<EventDto> getEvents(Long userId, Pageable page) {
        List<Event> events = eventRepository.findByInitiator_Id(userId, page).toList();
        return convertToListEventDto(events);
    }

    @Override
    public EventDto getEvent(Long userId, Long eventId) {
        Event currentEvent = findEventById(eventId);
        if (currentEvent.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("User don't have Event with id=" + eventId);
        }
        return eventDtoFromEvent(currentEvent);
    }

    @Override
    public EventDto findEvent(Long eventId) {
        return eventDtoFromEvent(findEventById(eventId));
    }

    private Event findEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + "was not found"));

    }
}
