package ru.practicum.service.imp;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.RequestDto;
import ru.practicum.dto.mapper.RequestMapper;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.event.EventOwnerException;
import ru.practicum.exception.event.EventParticipantException;
import ru.practicum.exception.event.EventStateException;
import ru.practicum.exception.request.RequestRepeatException;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.model.status.RequestStatus;
import ru.practicum.model.status.StateEvent;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.RequestService;

import java.util.List;
import java.util.Optional;

import static ru.practicum.dto.mapper.RequestMapper.convertRequestDtoToRequest;

@Service
@RequiredArgsConstructor
public class RequestServiceImp implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public RequestDto addRequest(RequestDto requestDto) {
        User currentUser = userRepository.findById(requestDto.getRequester())
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + requestDto.getRequester() + " не найден."));
        Event currentEvent = eventRepository.findById(requestDto.getEvent())
                .orElseThrow(() -> new NotFoundException("Событие с id=" + requestDto.getEvent() + " не найдено."));
        validRequest(requestDto, currentUser, currentEvent);
        if (currentEvent.getRequestModeration()) {
            requestDto.setStatus(RequestStatus.PENDING);
        }
        requestDto.setStatus(RequestStatus.ACCEPTED);
        Request newRequest = convertRequestDtoToRequest(requestDto, currentUser, currentEvent);
        requestRepository.save(newRequest);
        requestDto.setId(newRequest.getId());
        return requestDto;
    }

    @Override
    public List<RequestDto> getRequestsUser(Long userId) {
        List<Request> requests = requestRepository.findByRequester_Id(userId);
        return RequestMapper.convertToRequestDtoList(requests);
    }

    @Override
    public RequestDto rejectRequest(Long userId, Long requestId) {
        Optional<Request> currentRequest = requestRepository.findByRequester_IdAndEvent_Id(userId, requestId);
        if (currentRequest.isEmpty()) {
            throw new NotFoundException("Пользователь не подавал заявку на участие в событии," +
                    " поэтому нельзя отменить участие в том в чем не участвуешь");
        }
        Request request = currentRequest.get();
        request.setStatus(RequestStatus.REJECTED);
        requestRepository.save(request);
        return RequestMapper.convertToRequestDto(request);
    }

    private void validRequest(RequestDto requestDto, User currentUser, Event currentEvent) {
        if (currentEvent.getInitiator().getId().equals(currentUser.getId())) {
            throw new EventOwnerException("Инициатор события не может добавить запрос на участие в своём событии");
        }

        if (currentEvent.getState() != StateEvent.PUBLISHED) {
            throw new EventStateException("Нельзя участвовать в неопубликованном событии");
        }

        Optional<Request> request = requestRepository.findByRequester_IdAndEvent_Id(requestDto.getRequester(), requestDto.getEvent());
        if (request.isPresent()) {
            if (request.get().getStatus() == RequestStatus.PENDING) {
                throw new RequestRepeatException("Нельзя добавить повторный запрос");
            }
        }

        long potentialParticipants = requestRepository
                .countPotentialParticipants(queryFactory, currentEvent.getId());
        if (potentialParticipants >= currentEvent.getParticipantLimit()) {
            throw new EventParticipantException("Достигнут лимит запросов на участие");
        }
    }
}
