package ru.practicum.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.QRequest;
import ru.practicum.model.Request;
import ru.practicum.model.status.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByRequester_IdAndEvent_Id(Long requester, Long event);

    List<Request> findByRequester_Id(Long userId);

    default Long countPotentialParticipants(JPAQueryFactory queryFactory, Long eventId) {
        QRequest request = QRequest.request;
        return queryFactory.select(request)
                .where(request.event.id.eq(eventId)
                        .and(request.status.ne(RequestStatus.CANCELED))
                ).stream().count();
    }
}