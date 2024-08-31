package ru.practicum.service;

import ru.practicum.dto.RequestDto;
import ru.practicum.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;

import java.util.List;

public interface RequestService {
    RequestDto addRequest(RequestDto requestDto);

    List<RequestDto> getRequestsUser(Long userId);

    RequestDto cancelRequest(Long userId, Long requestId);

    List<RequestDto> getRequestByEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatusRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
