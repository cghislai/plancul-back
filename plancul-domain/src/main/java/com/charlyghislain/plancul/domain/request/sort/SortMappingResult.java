package com.charlyghislain.plancul.domain.request.sort;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SortMappingResult<U extends Comparable<?>> {
    private Expression<U> sortExpression;
    private List<Predicate> predicates = new ArrayList<>();

    public SortMappingResult(Expression<U> sortExpression) {
        this.sortExpression = sortExpression;
    }

    public SortMappingResult(Expression<U> sortExpression, Predicate... predicates) {
        this.sortExpression = sortExpression;
        this.predicates = Collections.unmodifiableList(Arrays.asList(predicates));
    }

    public Expression<U> getSortExpression() {
        return sortExpression;
    }

    public List<Predicate> getPredicates() {
        return predicates;
    }
}
