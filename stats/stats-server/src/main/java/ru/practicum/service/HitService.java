package ru.practicum.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.URLParameter;
import ru.practicum.dto.ViewStats;
import ru.practicum.model.Application;
import ru.practicum.model.Hit;
import ru.practicum.model.IPAddress;
import ru.practicum.model.QHit;
import ru.practicum.repository.ApplicationRepository;
import ru.practicum.repository.HitRepository;
import ru.practicum.repository.IPAddressRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HitService {
    private final EntityManager entityManager;

    private final HitRepository hitRepository;
    private final IPAddressRepository ipAddressRepository;
    private final ApplicationRepository applicationRepository;


    public void saveHit(EndpointHit endpointHit) {
        Application application = applicationRepository.findByName(endpointHit.getApp())
                .orElseGet(() -> applicationRepository.save(
                        Application.builder().name(endpointHit.getApp()).build())
                );

        IPAddress ipAddress = ipAddressRepository.findByAddress(endpointHit.getIp())
                .orElseGet(() -> ipAddressRepository.save(
                        IPAddress.builder().address(endpointHit.getIp()).build())
                );

        Hit hit = Hit.builder()
                .app(application)
                .ip(ipAddress)
                .time(endpointHit.getTimestamp())
                .uri(endpointHit.getUri())
                .build();

        hitRepository.save(hit);
    }

    public List<ViewStats> getStatistic(URLParameter parameter) {
        QHit hit = QHit.hit;

        JPAQuery<ViewStats> query = new JPAQuery<>(entityManager);

        JPAQuery<ViewStats> resultQuery = query.select(Projections.constructor(
                                ViewStats.class,
                                hit.app.name,
                                hit.uri,
                                parameter.getUnique()
                                        ? hit.ip.address.countDistinct()
                                        : hit.ip.address.count()
                        )
                )
                .from(hit)
                .where(hit.time.between(parameter.getStart(), parameter.getEnd()));

        BooleanBuilder conditionForSearch = new BooleanBuilder();

        for (String condition : parameter.getUris()) {
            conditionForSearch.or(hit.uri.eq(condition));
        }

        resultQuery.where(conditionForSearch);

        if (parameter.getUnique()) {
            query.groupBy(hit.app.name, hit.uri, hit.ip.address);
        } else {
            query.groupBy(hit.app.name, hit.uri);
        }

        resultQuery.orderBy(hit.uri.count().desc());

        return query.fetch();
    }
}