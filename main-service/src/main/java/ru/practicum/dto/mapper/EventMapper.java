package ru.practicum.dto.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.LocationDto;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.EventUpdateDto;
import ru.practicum.dto.event.StateEventDto;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;
import ru.practicum.model.status.StateEvent;

import java.util.List;

import static ru.practicum.dto.mapper.CategoryMapper.categoryFromCategoryDto;
import static ru.practicum.dto.mapper.LocationMapper.locationDtoFromLocation;
import static ru.practicum.dto.mapper.LocationMapper.locationFromLocationDto;
import static ru.practicum.dto.mapper.UserMapper.userFromUserDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {
    public static Event eventFromEventDto(EventDto eventDto,
                                          UserDto userDto,
                                          CategoryDto categoryDto,
                                          LocationDto locationDto) {
        User currentUser = userDto != null
                ? userFromUserDto(userDto)
                : null;

        Category currentCategory = categoryDto != null
                ? categoryFromCategoryDto(categoryDto)
                : null;

        Location currentLocation = locationDto != null
                ? locationFromLocationDto(locationDto)
                : null;
        return Event.builder()
                .initiator(currentUser)
                .category(currentCategory)
                .title(eventDto.getTitle())
                .annotation(eventDto.getAnnotation())
                .description(eventDto.getDescription())
                .paid(eventDto.getPaid())
                .requestModeration(eventDto.getRequestModeration())
                .participantLimit(eventDto.getParticipantLimit())
                .state(eventDto.getState())
                .location(currentLocation)
                .createdOn(eventDto.getCreatedOn())
                .time(eventDto.getEventDate())
                .build();
    }

    public static EventDto eventDtoFromEvent(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .initiator(event.getInitiator().getId())
                .category(event.getCategory().getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .paid(event.getPaid())
                .requestModeration(event.getRequestModeration())
                .participantLimit(event.getParticipantLimit())
                .state(event.getState())
                .location(locationDtoFromLocation(event.getLocation()))
                .createdOn(event.getCreatedOn())
                .eventDate(event.getTime())
                .build();
    }

    public static Event updatedEvent(final Event event, EventUpdateDto eventUpdateDto) {
        if (eventUpdateDto.getTitle() != null) {
            event.setTitle(eventUpdateDto.getTitle());
        }
        if (eventUpdateDto.getAnnotation() != null) {
            event.setAnnotation(eventUpdateDto.getAnnotation());
        }
        if (eventUpdateDto.getDescription() != null) {
            event.setDescription(eventUpdateDto.getDescription());
        }
        if (eventUpdateDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdateDto.getParticipantLimit());
        }
        if (eventUpdateDto.getEventDate() != null) {
            event.setTime(eventUpdateDto.getEventDate());
        }
        if (eventUpdateDto.getLocation() != null) {
            event.setLocation(LocationMapper.locationFromLocationDto(eventUpdateDto.getLocation()));
        }
        if (eventUpdateDto.getPaid() != null) {
            event.setPaid(eventUpdateDto.getPaid());
        }
        if (eventUpdateDto.getCreatedOn() != null) {
            event.setCreatedOn(eventUpdateDto.getCreatedOn());
        }
        if (eventUpdateDto.getStateAction() != null) {
            if (eventUpdateDto.getStateAction() == StateEventDto.CANCEL_REVIEW) {
                event.setState(StateEvent.CANCELED);
            }
            event.setState(StateEvent.WAITING);
        }
        return event;
    }

    public static List<EventDto> convertToListEventDto(List<Event> list) {
        return list.stream().map(EventMapper::eventDtoFromEvent).toList();
    }
}
