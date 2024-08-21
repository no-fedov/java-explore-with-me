package ru.practicum.service;

import ru.practicum.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto addRequest(RequestDto requestDto);

    List<RequestDto> getRequestsUser(Long userId);

}
