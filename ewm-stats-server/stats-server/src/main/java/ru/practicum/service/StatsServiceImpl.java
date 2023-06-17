package ru.practicum.service;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exeption.ValidateException;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.EndpointHitFilter;
import ru.practicum.model.EndpointHitMapper;
import ru.practicum.storage.EndpointHitsRepository;
import ru.practicum.utils.QPredicate;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.model.QEndpointHit.endpointHit;

@Service
@Slf4j
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final EndpointHitsRepository repository;
    private final JPAQueryFactory queryFactory;

    public StatsServiceImpl(EndpointHitsRepository repository, EntityManager em) {
        this.repository = repository;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    @Transactional
    public void saveHit(EndpointHitDto dto) {
        final EndpointHit endpointHit = EndpointHitMapper.fromDto(dto);
        repository.save(endpointHit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new ValidateException("Start time must be before end end");
        }
        final EndpointHitFilter filter = EndpointHitFilter.builder()
                .timestampAfter(start)
                .timestampBefore(end)
                .uris(uris)
                .build();

        return getByFilter(unique, filter);
    }

    private List<ViewStatsDto> getByFilter(Boolean unique, EndpointHitFilter filter) {
        final Predicate predicate = getEndpointHitPredicate(filter);
        final ConstructorExpression<ViewStatsDto> expression =
                Projections.constructor(
                        ViewStatsDto.class,
                        endpointHit.app,
                        endpointHit.uri,
                        Boolean.TRUE.equals(unique)
                                ? endpointHit.ip.countDistinct()
                                : endpointHit.count()
                );
        return queryFactory.from(endpointHit)
                .select(expression)
                .where(predicate)
                .groupBy(endpointHit.app, endpointHit.uri)
                .fetch();
    }

    private Predicate getEndpointHitPredicate(EndpointHitFilter filter) {
        return QPredicate.builder()
                .add(filter.getIp(), endpointHit.ip::eq)
                .add(filter.getApp(), endpointHit.app::eq)
                .add(filter.getUris(), endpointHit.uri::in)
                .add(filter.getTimestampAfter(), endpointHit.timestamp::after)
                .add(filter.getTimestampBefore(), endpointHit.timestamp::before)
                .buildAnd();
    }
}