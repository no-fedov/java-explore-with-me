package ru.practicum.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.StateActionUser;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.mapper.EventMapper;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.EventActionException;
import ru.practicum.exception.NoValidParameter;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.status.StateEvent;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.CategoryService;
import ru.practicum.service.EventService;
import ru.practicum.service.LocationService;
import ru.practicum.service.UserService;

import java.time.LocalDateTime;
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
    public EventShortDto addEvent(Long userId, NewEventDto eventDto) {
        UserDto currentUserDto = userService.findUser(userId);
        CategoryDto currentCategoryDto = null;
        if (eventDto.getCategory() != null) {
            currentCategoryDto = categoryService.findCategory(eventDto.getCategory());
        }
        locationService.addLocation(eventDto.getLocation());
        Event newEvent = EventMapper.eventFromNewEventDto(eventDto,
                currentUserDto,
                currentCategoryDto,
                eventDto.getLocation());
        newEvent.setCreatedOn(LocalDateTime.now());
        newEvent.setState(StateEvent.PENDING);
        System.out.println("НАЧИНАЕМ СОХРАНЯТЬ");
        eventRepository.save(newEvent);
        System.out.println("СОХРАНИЛИ");
        return eventDtoFromEvent(newEvent);
    }

    @Override
    public EventShortDto updateEvent(UpdateEventUserRequest eventUpdateDto) {
        Event event = findEventById(eventUpdateDto.getId());

        if (eventUpdateDto.getCategory() != null) {
            categoryService.findCategory(eventUpdateDto.getCategory());
        }

        //обновить категории забыл я
        if (!event.getInitiator().getId().equals(eventUpdateDto.getInitiator())) {
            throw new NotFoundException("Event with id=" + eventUpdateDto.getId() + "was not found");
        }

        if (event.getState() == StateEvent.PUBLISHED) {
            throw new EventActionException("Нельзя изменять опубликованное событие");
        }

        if (eventUpdateDto.getEventDate() != null && eventUpdateDto.getEventDate().isBefore(LocalDateTime.now().minusHours(2))) {
            throw new NoValidParameter("дата и время на которые намечено событие не может быть раньше," +
                    " чем через два часа от текущего момента");
        }

        if (eventUpdateDto.getStateAction() != null) {
            event.setState(eventUpdateDto.getStateAction() == StateActionUser.CANCEL_REVIEW
                    ? StateEvent.CANCELED
                    : StateEvent.PENDING);
        }
        convertToEventFromUpdateEventUserRequest(event, eventUpdateDto);
        eventRepository.save(event);
        return eventDtoFromEvent(event);
    }

    @Override
    public List<EventShortDto> getEvents(Long userId, Pageable page) {
        List<Event> events = eventRepository.findByInitiator_Id(userId, page).toList();
        return convertToListEventDto(events);
    }

    @Override
    public EventShortDto getEvent(Long userId, Long eventId) {
        Event currentEvent = findEventById(eventId);
        if (!currentEvent.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("User don't have Event with id=" + eventId);
        }
        return eventDtoFromEvent(currentEvent);
    }

    @Override
    public EventShortDto findEvent(Long eventId) {
        return eventDtoFromEvent(findEventById(eventId));
    }

    private Event findEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + "was not found"));
    }
}