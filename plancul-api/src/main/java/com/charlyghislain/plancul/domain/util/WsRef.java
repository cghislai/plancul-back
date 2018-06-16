package com.charlyghislain.plancul.domain.util;

public class WsRef<T extends WsDomainEntity> {
    private long id;

    public WsRef(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
