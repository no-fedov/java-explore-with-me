package ru.practicum.controller.privateapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.RequestDto;
import ru.practicum.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class RequestPrivateController {
    private final RequestService requestService;

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto postRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("POST /users/{userId}/requests  userId = {}, eventId = {}", userId, eventId);
        return requestService.addRequest(userId, eventId);
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
