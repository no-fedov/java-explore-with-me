package ru.practicum.controller.private_api;

import lombok.RequiredArgsConstructor;
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
public class RequestPrivateController {
    private final RequestService requestService;

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto postRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        RequestDto requestDto = RequestDto.builder()
                .requester(userId)
                .event(eventId)
                .created(LocalDateTime.now())
                .status(PENDING)
                .build();
        return requestService.addRequest(requestDto);
    }

    @GetMapping("/{userId}/requests")
    public List<RequestDto> getRequestsUser(@PathVariable Long userId) {
        return requestService.getRequestsUser(userId);
    }

//    @GetMapping("/{userId}/requests")
//    public List<RequestDto> getRequests(@PathVariable Long userId) {
//        return requestService.findRequestsByUserId(userId);
//    }
//
//    @PatchMapping("/{userId}/requests/{requestId}/cancel")
//    public RequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
//        return requestService.removeRequest(userId, requestId);
//    }

}
