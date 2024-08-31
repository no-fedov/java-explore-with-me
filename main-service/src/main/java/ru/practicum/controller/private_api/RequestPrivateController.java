package ru.practicum.controller.private_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.RequestDto;
import ru.practicum.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.model.status.RequestStatus.PENDING;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class RequestPrivateController {
    private final RequestService requestService;

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto postRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        RequestDto requestDto = RequestDto.builder()
                .requester(userId)
                .event(eventId)
                .created(LocalDateTime.now())
                .build();
        log.info("POST /users/{userId}/requests  userId = {}, body = {}", userId, requestDto);
        return requestService.addRequest(requestDto);
    }

    @GetMapping("/{userId}/requests")
    public List<RequestDto> getRequestsUser(@PathVariable Long userId) {
        log.info("GET /users/{userId}/requests userId = {}", userId);
        return requestService.getRequestsUser(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("PATCH users/{userId}/requests/{requestId}/cancel userId = {} requestId = {}", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }
}
