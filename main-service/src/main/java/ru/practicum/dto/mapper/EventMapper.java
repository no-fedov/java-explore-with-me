package ru.practicum.dto.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.LocationDto;
import ru.practicum.dto.event.*;
import ru.practicum.dto.user.UserDto;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.dto.mapper.CategoryMapper.categoryDtoFromCategory;
import static ru.practicum.dto.mapper.CategoryMapper.categoryFromCategoryDto;
import static ru.practicum.dto.mapper.LocationMapper.locationDtoFromLocation;
import static ru.practicum.dto.mapper.LocationMapper.locationFromLocationDto;
import static ru.practicum.dto.mapper.UserMapper.convertToUserShortDtoFromUser;
import static ru.practicum.dto.mapper.UserMapper.userFromUserDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {
    public static Event eventFromNewEventDto(NewEventDto eventDto,
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
                .location(currentLocation)
                .createdOn(LocalDateTime.now())
                .time(eventDto.getEventDate())
                .build();
    }

    public static EventShortDto eventDtoFromEvent(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .initiator(UserMapper.convertToUserShortDtoFromUser(event.getInitiator()))
                .category(CategoryMapper.categoryDtoFromCategory(event.getCategory()))
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

    public static Event convertToEventFromUpdateEventUserRequest(final Event event, UpdateEventUserRequest eventUpdateDto) {
        if (eventUpdateDto.getTitle() != null && !eventUpdateDto.getTitle().isBlank()) {
            event.setTitle(eventUpdateDto.getTitle());
        }
        if (eventUpdateDto.getAnnotation() != null && !eventUpdateDto.getAnnotation().isBlank()) {
            event.setAnnotation(eventUpdateDto.getAnnotation());
        }
        if (eventUpdateDto.getDescription() != null && !eventUpdateDto.getDescription().isBlank()) {
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

        return event;
    }

    public static List<EventShortDto> convertToListEventDto(List<Event> list) {
        return list.stream().map(EventMapper::eventDtoFromEvent).toList();
    }

    public static Event convertToUpdatedEventDtoFromEventAndUpdateEventAdmin(Event event,
                                                                             UpdateEventAdminRequest eventDto) {
        if (eventDto.getAnnotation() != null && !eventDto.getAnnotation().isBlank()) {
            event.setAnnotation(eventDto.getAnnotation());
        }

        if (eventDto.getDescription() != null && !eventDto.getDescription().isBlank()) {
            event.setDescription(eventDto.getDescription());
        }

        if (eventDto.getTitle() != null && !eventDto.getTitle().isBlank()) {
            event.setTitle(eventDto.getTitle());
        }

        if (eventDto.getEventDate() != null) {
            event.setTime(eventDto.getEventDate());
        }
        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }

        if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }

        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }
        return event;
    }

    public static EventFullDto convertToEventFullDtoFromEvent(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .initiator(convertToUserShortDtoFromUser(event.getInitiator()))
                .category(categoryDtoFromCategory(event.getCategory()))
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
}
