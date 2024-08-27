package ru.practicum.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.EventAdminService;

@Service
@RequiredArgsConstructor
public class EventAdminServiceImp implements EventAdminService {
    private final EventRepository eventRepository;
}
