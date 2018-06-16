package com.charlyghislain.plancul.converter.util;

import com.charlyghislain.plancul.domain.util.DomainEntity;
import com.charlyghislain.plancul.domain.util.WsDomainEntity;
import com.charlyghislain.plancul.domain.util.WsRef;

public interface WsDomainObjectConverter<D extends DomainEntity, W extends WsDomainEntity> {

    default WsRef<W> reference(D entity) {
        return new WsRef<>(entity.getId());
    }

    D load(WsRef<W> ref);

    W toWsEntity(D entity);

    D fromWsEntity(W wsEntity);

    void updateEntity(D entity, W wsEntity);
}
