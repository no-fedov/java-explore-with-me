package ru.practicum.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.QRequest;
import ru.practicum.model.Request;
import ru.practicum.model.status.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByIdAndRequester_Id(Long requestId, Long requester);

    List<Request> findByRequester_IdAndEvent_Id(Long requester, Long event);

    List<Request> findByRequester_Id(Long userId);

    default Long countPotentialParticipants(JPAQueryFactory queryFactory, Long eventId) {
        QRequest request = QRequest.request;
        return queryFactory.select(request.count())
                .from(request)
                .where(request.event.id.eq(eventId)
                        .and(request.status.ne(RequestStatus.REJECTED)))
                .fetchOne();
    }
}