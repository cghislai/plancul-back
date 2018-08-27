package com.charlyghislain.plancul.domain.util;


import java.util.Optional;

public interface WithId {

    Long getId();

    default Optional<Long> getIdOptional() {
        return Optional.ofNullable(this.getId());
    }

}
