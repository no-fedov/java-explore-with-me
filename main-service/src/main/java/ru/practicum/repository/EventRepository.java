package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Event;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByInitiator_Id(Long userId, Pageable page);

    Optional<Event> findByIdAndInitiator_Id(Long eventId, Long userId);

    Long countByCategory_Id(Long categoryId);
}
