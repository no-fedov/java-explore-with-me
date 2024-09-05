package ru.practicum.repository.custom.imp;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Request;
import ru.practicum.model.status.RequestStatus;
import ru.practicum.repository.custom.RequestPrivateRepository;

import java.util.List;
import java.util.Set;

import static ru.practicum.model.QRequest.request;

@Repository
@RequiredArgsConstructor
public class RequestPrivateRepositoryImpl implements RequestPrivateRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Request> getRequestByEvent(Long userId, Long eventId) {
        return queryFactory
                .select(request)
                .from(request)
                .where(request.event.id.eq(eventId)
                        .and(request.event.initiator.id.eq(userId)))
                .fetch();
    }

    @Override
    public List<Request> getPendingRequestsForEvent(Set<Long> requestIds, Long eventId) {
        return queryFactory.select(request).from(request)
                .where(request.event.id.eq(eventId)
                        .and(request.id.in(requestIds))
                        .and(request.status.eq(RequestStatus.PENDING)))
                .fetch();
    }

    @Override
    public Long getCountParticipants(Long eventId) {
        return queryFactory.select(request.count())
                .from(request)
                .where(request.event.id.eq(eventId)
                        .and(request.status.eq(RequestStatus.CONFIRMED)))
                .fetchOne();
    }

    @Override
    public Long getConfirmedCountListRequestOfEvent(Set<Long> requestIds, Long eventId) {
        return queryFactory.select(request.count())
                .from(request)
                .where(request.event.id.eq(eventId)
                        .and(request.id.in(requestIds))
                        .and(request.status.eq(RequestStatus.CONFIRMED)))
                .fetchOne();
    }
}