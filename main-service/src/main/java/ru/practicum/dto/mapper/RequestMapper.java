package ru.practicum.dto.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.RequestDto;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static Request convertRequestDtoToRequest(RequestDto requestDto, User user, Event event) {
        return Request.builder()
                .id(requestDto.getId())
                .requester(user)
                .event(event)
                .created(requestDto.getCreated())
                .status(requestDto.getStatus())
                .build();
    }
}
