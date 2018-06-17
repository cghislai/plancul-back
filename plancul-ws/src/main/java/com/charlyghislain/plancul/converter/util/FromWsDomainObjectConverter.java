package com.charlyghislain.plancul.converter.util;

import com.charlyghislain.plancul.domain.util.DomainEntity;
import com.charlyghislain.plancul.domain.util.WsDomainEntity;
import com.charlyghislain.plancul.domain.util.WsRef;

public interface FromWsDomainObjectConverter<D extends DomainEntity, W extends WsDomainEntity> {

    D load(WsRef<W> ref);

    D fromWsEntity(W wsEntity);

    void updateEntity(D entity, W wsEntity);
}
