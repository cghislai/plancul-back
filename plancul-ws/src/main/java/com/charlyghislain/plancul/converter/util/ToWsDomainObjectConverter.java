package com.charlyghislain.plancul.converter.util;

import com.charlyghislain.plancul.domain.util.DomainEntity;
import com.charlyghislain.plancul.domain.util.WsDomainEntity;
import com.charlyghislain.plancul.domain.util.WsRef;

public interface ToWsDomainObjectConverter<D extends DomainEntity, W extends WsDomainEntity> {

    default WsRef<W> reference(D entity) {
        return new WsRef<>(entity.getId());
    }

    W toWsEntity(D entity);
}
