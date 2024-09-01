package ru.practicum.service.imp;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.RequestDto;
import ru.practicum.dto.event.EventRequestStatus;
import ru.practicum.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.mapper.RequestMapper;
import ru.practicum.exception.EventActionException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.RequestActionException;
import ru.practicum.model.Event;
import ru.practicum.model.QRequest;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.model.status.RequestStatus;
import ru.practicum.model.status.StateEvent;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.RequestService;

import java.util.List;

import static ru.practicum.dto.mapper.RequestMapper.convertToRequestDto;
import static ru.practicum.dto.mapper.RequestMapper.convertToRequestFromEventAndUser;


@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImp implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final JPAQueryFactory queryFactory;
    private final QRequest request = QRequest.request;

    @Transactional
    @Override
    public RequestDto addRequest(Long userId, Long eventId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден."));
        Event currentEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено."));
        validRequest(currentUser, currentEvent);
        Request newRequest = convertToRequestFromEventAndUser(currentUser, currentEvent);
        setStatus(newRequest);
        requestRepository.save(newRequest);
        RequestDto requestDto = convertToRequestDto(newRequest);
        log.info("Save request {}", requestDto);
        return requestDto;
    }

    @Transactional
    @Override
    public List<RequestDto> getRequestsUser(Long userId) {
        List<Request> requests = requestRepository.findByRequester_Id(userId);
        return RequestMapper.convertToRequestDtoList(requests);
    }

    @Transactional
    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        List<Request> currentRequest = requestRepository.findByIdAndRequester_Id(requestId, userId);
        if (currentRequest.isEmpty()) {
            throw new NotFoundException("Пользователь не подавал заявку на участие в событии " +
                    "поэтому нельзя отменить участие в том в чем не участвуешь");
        }
        Request request = currentRequest.getFirst();
        request.setStatus(RequestStatus.CANCELED);
        requestRepository.save(request);
        return convertToRequestDto(request);
    }

    @Transactional
    @Override
    public List<RequestDto> getRequestByEvent(Long userId, Long eventId) {
        JPAQuery<Request> query = queryFactory
                .select(request)
                .from(request)
                .where(request.event.id.eq(eventId))
                .where(request.event.initiator.id.eq(userId));
        List<Request> request = query.fetch();
        return RequestMapper.convertToRequestDtoList(request);
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateStatusRequest(Long userId,
                                                              Long eventId,
                                                              EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new NotFoundException("У пользователя c номером {} нет события под id = {}"));

        if (event.getParticipantLimit() != 0
                && eventRequestStatusUpdateRequest.getStatus() == EventRequestStatus.CONFIRMED
                && requestRepository.countParticipants(queryFactory, eventId) >= event.getParticipantLimit()) {
            throw new RequestActionException("нельзя подтвердить заявку, " +
                    "если уже достигнут лимит по заявкам на данное событие");
        }

        if (eventRequestStatusUpdateRequest.getStatus() == EventRequestStatus.REJECTED
                && requestRepository.containConfirmedRequestInList(queryFactory, eventRequestStatusUpdateRequest.getRequestIds()) > 0) {
            throw new RequestActionException("Попытка отменить уже принятую заявку на участие в событии");
        }

        List<Request> requests = queryFactory.select(request).from(request)
                .where(request.event.id.eq(eventId))
                .where(request.id.in(eventRequestStatusUpdateRequest.getRequestIds()))
                .where(request.status.eq(RequestStatus.PENDING)).fetch();

        if (eventRequestStatusUpdateRequest.getStatus() == EventRequestStatus.REJECTED) {
            requests.forEach(r -> r.setStatus(RequestStatus.REJECTED));
            requestRepository.saveAll(requests);
            return EventRequestStatusUpdateResult.builder()
                    .confirmedRequests(List.of())
                    .rejectedRequests(RequestMapper.convertToRequestDtoList(requests))
                    .build();
        }

        int confirmedCount = 0;
        Long participantLimit = event.getParticipantLimit();
        Long confirmedParticipants = requestRepository.countParticipants(queryFactory, eventId);

        for (Request req : requests) {
            if (confirmedParticipants >= participantLimit && participantLimit > 0) {
                break;
            }
            req.setStatus(RequestStatus.CONFIRMED);
            confirmedParticipants++;
            confirmedCount++;
        }

        List<Request> confirmedRequests = requests.subList(0, confirmedCount);
        List<Request> rejectedRequests = requests.subList(confirmedCount, requests.size());

        rejectedRequests.forEach(r -> r.setStatus(RequestStatus.REJECTED));
        requestRepository.saveAll(requests);

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(RequestMapper.convertToRequestDtoList(confirmedRequests))
                .rejectedRequests(RequestMapper.convertToRequestDtoList(rejectedRequests))
                .build();
    }

    private void validRequest(User currentUser, Event currentEvent) {
        if (currentEvent.getInitiator().getId().equals(currentUser.getId())) {
            throw new RequestActionException("Инициатор события не может добавить запрос на участие в своём событии");
        }

        if (currentEvent.getState() != StateEvent.PUBLISHED) {
            throw new EventActionException("Нельзя участвовать в неопубликованном событии");
        }

        List<Request> request = requestRepository.findByRequester_IdAndEvent_Id(currentUser.getId(), currentEvent.getId());
        if (!request.isEmpty()) {
            if (request.getFirst().getStatus() == RequestStatus.PENDING
                    || request.getFirst().getStatus() == RequestStatus.CONFIRMED) {
                throw new RequestActionException("Нельзя добавить повторный запрос");
            }
        }

        Long participants = requestRepository
                .countParticipants(queryFactory, currentEvent.getId());

        if (participants >= currentEvent.getParticipantLimit() && currentEvent.getParticipantLimit() != 0) {
            throw new RequestActionException("Достигнут лимит участников");
        }
    }

    private void setStatus(Request request) {
        Event currentEvent = request.getEvent();

        if (currentEvent.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
            return;
        } else if (currentEvent.getRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
            return;
        }
        request.setStatus(RequestStatus.CONFIRMED);
    }
}