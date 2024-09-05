package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Request;
import ru.practicum.repository.custom.RequestPrivateRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long>, RequestPrivateRepository {
    List<Request> findByIdAndRequester_Id(Long requestId, Long requester);

    List<Request> findByRequester_IdAndEvent_Id(Long requester, Long event);

    List<Request> findByRequester_Id(Long userId);
}