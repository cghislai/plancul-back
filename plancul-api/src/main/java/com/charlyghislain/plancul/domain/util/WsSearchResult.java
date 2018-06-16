package com.charlyghislain.plancul.domain.util;

import java.util.List;

public class WsSearchResult<W extends WsDomainEntity> {
    private List<WsRef<W>> list;
    private long count;

    public List<WsRef<W>> getList() {
        return list;
    }

    public void setList(List<WsRef<W>> list) {
        this.list = list;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
