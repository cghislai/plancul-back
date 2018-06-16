package com.charlyghislain.plancul.service;

import com.charlyghislain.plancul.domain.util.NoIdException;
import com.charlyghislain.plancul.domain.util.UnexpectedIdException;
import com.charlyghislain.plancul.domain.util.WithId;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class ValidationService {

    void validateNoId(WithId entity) {
        Optional<Long> idOptional = entity.getIdOptional();
        if (idOptional.isPresent()) {
            throw new UnexpectedIdException();
        }
    }

    void validateNonNullId(WithId entity) {
        Optional<Long> idOptional = entity.getIdOptional();
        if (!idOptional.isPresent()) {
            throw new NoIdException();
        }
    }
}
