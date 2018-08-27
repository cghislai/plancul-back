package com.charlyghislain.plancul.api.domain.util;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;

public interface WsDomainEntity extends Serializable {

    @Nullable
    @NullableField
    Long getId();
}
