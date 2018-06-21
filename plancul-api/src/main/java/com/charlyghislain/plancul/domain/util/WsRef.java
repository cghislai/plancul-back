package com.charlyghislain.plancul.domain.util;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class WsRef<T extends WsDomainEntity> implements Serializable {
    @NotNull
    private long id;

    public WsRef() {
    }

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
