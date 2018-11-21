package com.charlyghislain.plancul.astronomy.cache.service;

import com.charlyghislain.plancul.astronomy.api.domain.AstronomyEvent;
import com.charlyghislain.plancul.astronomy.api.domain.AstronomyEventType;
import com.charlyghislain.plancul.astronomy.api.request.AstronomyEventFilter;
import com.charlyghislain.plancul.astronomy.api.request.TimePagination;
import com.charlyghislain.plancul.astronomy.cache.domain.CachedYear;
import com.charlyghislain.plancul.astronomy.cache.domain.CachedAstronomyEvent;
import com.charlyghislain.plancul.astronomy.cache.domain.CachedAstronomyEvent_;
import com.charlyghislain.plancul.astronomy.cache.domain.CachedYear_;
import com.charlyghislain.plancul.astronomy.service.CachedEventConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.*;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

/**
 * // TODO: use a request cache hazelcast or smth,
 */
@Stateless
public class AstronomyEventDao {

    public static final ZoneId UTC_ZONE = ZoneId.of("Z");
    private final Logger LOG = LoggerFactory.getLogger(AstronomyEventDao.class);

    @PersistenceContext(unitName = "plancul-astronomy-pu")
    private EntityManager entityManager;

    public boolean isCached(int year) {
        return this.findCacheStatus(year).isPresent();
    }

    public void clearEvents(Integer yearNullable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaDelete<CachedYear> cachedYearQuery = criteriaBuilder.createCriteriaDelete(CachedYear.class);
        Root<CachedYear> cachedYearRoot = cachedYearQuery.from(CachedYear.class);
        CriteriaDelete<CachedAstronomyEvent> eventQuery = criteriaBuilder.createCriteriaDelete(CachedAstronomyEvent.class);
        Root<CachedAstronomyEvent> eventRoot = eventQuery.from(CachedAstronomyEvent.class);

        if (yearNullable != null) {
            Path<Integer> yearPath = cachedYearRoot.get(CachedYear_.year);
            Predicate cachedYearPredicate = criteriaBuilder.equal(yearPath, yearNullable);
            cachedYearQuery.where(cachedYearPredicate);

            Path<LocalDateTime> eventTimePath = eventRoot.get(CachedAstronomyEvent_.localDateTimeUTC);
            LocalDateTime yearStartTime = LocalDateTime.of(yearNullable, 1, 1, 0, 0, 0);
            LocalDateTime yearEndTime = LocalDateTime.of(yearNullable + 1, 1, 1, 0, 0, 0);
            Predicate eventStartPredicate = criteriaBuilder.greaterThanOrEqualTo(eventTimePath, yearStartTime);
            Predicate eventEndPredicate = criteriaBuilder.lessThan(eventTimePath, yearEndTime);
            eventQuery.where(eventStartPredicate, eventEndPredicate);
        }
        Query cachedYearTypedQuery = entityManager.createQuery(cachedYearQuery);
        cachedYearTypedQuery.executeUpdate();

        Query eventTypedQuery = entityManager.createQuery(eventQuery);
        eventTypedQuery.executeUpdate();
    }

    public List<AstronomyEvent> searchEvents(AstronomyEventFilter eventFilter, TimePagination pagination) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CachedAstronomyEvent> query = criteriaBuilder.createQuery(CachedAstronomyEvent.class);
        Root<CachedAstronomyEvent> eventRoot = query.from(CachedAstronomyEvent.class);

        List<Predicate> predicates = new ArrayList<>();

        Path<AstronomyEventType> eventTypePath = eventRoot.get(CachedAstronomyEvent_.eventType);
        Set<AstronomyEventType> typeWhiteList = eventFilter.getTypeWhiteList();
        if (!typeWhiteList.isEmpty()) {
            typeWhiteList.stream()
                    .map(type -> criteriaBuilder.equal(eventTypePath, type))
                    .reduce(criteriaBuilder::or)
                    .ifPresent(predicates::add);
        }

        Path<LocalDateTime> eventTimePath = eventRoot.get(CachedAstronomyEvent_.localDateTimeUTC);
        LocalDateTime pageStartTime = pagination.getPageStartTime();
        LocalDateTime pageEndTime = LocalDateTime.from(pageStartTime)
                .plus(pagination.getPageDuration(), pagination.getPageDurationUnit());
        Predicate pageStartPredicate = criteriaBuilder.greaterThanOrEqualTo(eventTimePath, pageStartTime);
        Predicate pageEndPredicate = criteriaBuilder.lessThan(eventTimePath, pageEndTime);
        predicates.add(pageStartPredicate);
        predicates.add(pageEndPredicate);

        Order eventTimeOrder = criteriaBuilder.asc(eventTimePath);
        query.orderBy(eventTimeOrder);

        predicates.stream()
                .reduce(criteriaBuilder::and)
                .ifPresent(query::where);

        TypedQuery<CachedAstronomyEvent> typedQuery = entityManager.createQuery(query);
        List<CachedAstronomyEvent> resultList = typedQuery.getResultList();
        return resultList.stream()
                .map(CachedEventConverter::toEvent)
                .collect(Collectors.toList());
    }

    /**
     * Append cache events if they are in continuation with the existing cache
     *
     * @param year   the year for which all events have been gathered
     * @param events the events to cache
     */
    public void cacheEvents(int year, List<AstronomyEvent> events) {
        clearEvents(year);

        events.stream()
                .map(CachedEventConverter::fromEvent)
                .forEach(entityManager::merge);

        CachedYear cachedYear = new CachedYear();
        cachedYear.setYear(year);
        entityManager.merge(cachedYear);

        LOG.info("Cached year {}", year);
    }


    private Optional<CachedYear> findCacheStatus(int year) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CachedYear> query = criteriaBuilder.createQuery(CachedYear.class);
        Root<CachedYear> yearRoot = query.from(CachedYear.class);

        Path<Integer> yearPath = yearRoot.get(CachedYear_.year);
        Predicate yearPredicate = criteriaBuilder.equal(yearPath, year);
        query.where(yearPredicate);

        TypedQuery<CachedYear> typedQuery = entityManager.createQuery(query);
        typedQuery.setMaxResults(1);
        return typedQuery.getResultList().stream()
                .findAny();
    }

}
