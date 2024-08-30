package ru.practicum.dto.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.RequestDto;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;

import java.util.List;

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

    public static RequestDto convertToRequestDto(Request request) {
        return RequestDto.builder()
                .status(request.getStatus())
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .build();
    }

    public static List<RequestDto> convertToRequestDtoList(List<Request> requests) {
        return requests.stream().map(RequestMapper::convertToRequestDto).toList();
    }
}
