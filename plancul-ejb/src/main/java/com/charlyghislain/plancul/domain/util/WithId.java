package com.charlyghislain.plancul.domain.util;


import com.charlyghislain.plancul.domain.util.exception.NoIdException;

import java.util.Optional;

public interface WithId {

    Long getId();

    default Optional<Long> getIdOptional() {
        return Optional.ofNullable(this.getId());
    }

    default long getIdOrThrow() {
        return this.getIdOptional()
                .orElseThrow(NoIdException::new);
    }

}
