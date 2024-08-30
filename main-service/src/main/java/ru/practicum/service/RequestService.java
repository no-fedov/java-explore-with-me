package ru.practicum.service;

import ru.practicum.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto addRequest(RequestDto requestDto);

    List<RequestDto> getRequestsUser(Long userId);

    RequestDto rejectRequest(Long userId, Long requestId);

    List<RequestDto> getRequestByEvent(Long userId, Long eventId);

    List<RequestDto> updateStatusRequest(Long userId, Long eventId, List<Long> requestIds);
}
