package com.charlyghislain.plancul.domain.util;

import java.util.List;

public class SearchResult<T extends DomainEntity> {

    private List<T> list;
    private long totalCount;

    public SearchResult(List<T> list, long totalCount) {
        this.list = list;
        this.totalCount = totalCount;
    }

    public List<T> getList() {
        return list;
    }

    public long getTotalCount() {
        return totalCount;
    }
}
