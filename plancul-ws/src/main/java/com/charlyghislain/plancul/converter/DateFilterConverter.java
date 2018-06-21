package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.domain.request.filter.DateFilter;
import com.charlyghislain.plancul.domain.api.request.filter.WsDateFilter;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DateFilterConverter {

    public DateFilter fromWsDateFilter(WsDateFilter wsDateFilter) {
        DateFilter dateFilter = new DateFilter();

        wsDateFilter.getNotBefore()
                .ifPresent(dateFilter::setNotBefore);

        wsDateFilter.getNotAfter()
                .ifPresent(dateFilter::setNotAfter);

        return dateFilter;
    }
}
