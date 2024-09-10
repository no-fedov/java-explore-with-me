package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Request;
import ru.practicum.model.status.RequestStatus;
import ru.practicum.repository.custom.RequestPrivateRepository;

import java.util.List;
import java.util.Set;

public interface RequestRepository extends JpaRepository<Request, Long>, RequestPrivateRepository {
    List<Request> findByIdAndRequester_Id(Long requestId, Long requester);

    boolean existsByRequester_IdAndEvent_IdAndStatusIn(Long requester, Long event, Set<RequestStatus> statuses);

    List<Request> findByRequester_Id(Long userId);
}