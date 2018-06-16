package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.util.DomainEntity;
import com.charlyghislain.plancul.domain.util.Pagination;
import com.charlyghislain.plancul.domain.util.SearchResult;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Stateless
public class SearchService {

    @PersistenceContext(unitName = "plancul-pu")
    private EntityManager entityManager;

    <T extends DomainEntity> SearchResult<T> search(Pagination pagination, CriteriaQuery<T> query, Root<T> root, List<Predicate> predicates) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);

        Expression<Long> count = criteriaBuilder.count(root);
        countQuery.select(count);
        countQuery.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQuery);
        Long countResult = countTypedQuery.getSingleResult();


        query.select(root);
        query.where(predicates.toArray(new Predicate[0]));

        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(pagination.getOffset());
        typedQuery.setMaxResults(pagination.getSize());
        List<T> resultList = typedQuery.getResultList();

        SearchResult<T> result = new SearchResult<>(resultList, countResult);
        return result;
    }
}
