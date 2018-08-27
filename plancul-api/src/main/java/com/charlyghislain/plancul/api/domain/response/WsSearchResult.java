package com.charlyghislain.plancul.api.domain.response;

import com.charlyghislain.plancul.api.domain.util.WsDomainEntity;
import com.charlyghislain.plancul.api.domain.util.WsRef;

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
