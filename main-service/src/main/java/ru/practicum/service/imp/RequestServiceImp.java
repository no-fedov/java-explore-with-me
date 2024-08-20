package ru.practicum.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.RequestDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.RequestService;

import static ru.practicum.dto.mapper.RequestMapper.convertRequestDtoToRequest;

@Service
@RequiredArgsConstructor
public class RequestServiceImp implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public RequestDto addRequest(RequestDto requestDto) {

        User currentUser = userRepository.findById(requestDto.getRequester())
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + requestDto.getRequester() + " не найден."));
        Event currentEvent = eventRepository.findById(requestDto.getEvent())
                .orElseThrow(() -> new NotFoundException("Событие с id=" + requestDto.getRequester() + " не найдено."));

        Request request = convertRequestDtoToRequest(requestDto, currentUser, currentEvent);
        requestRepository.save(request);
        requestDto.setId(request.getId());
        return requestDto;
    }
}
