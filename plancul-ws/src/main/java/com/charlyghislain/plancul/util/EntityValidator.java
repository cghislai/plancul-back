package com.charlyghislain.plancul.util;

import com.charlyghislain.plancul.api.domain.util.WsDomainEntity;
import com.charlyghislain.plancul.domain.util.DomainEntity;
import com.charlyghislain.plancul.util.exception.WsValidationException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class EntityValidator {

    @Inject
    private Validator validator;

    public void validate(WsDomainEntity wsDomainEntity, DomainEntity domainEntity) {
        Set<ConstraintViolation<?>> errors = new HashSet<>();
        Set<ConstraintViolation<WsDomainEntity>> wsViolations = this.validator.validate(wsDomainEntity);
        errors.addAll(wsViolations);

        if (!errors.isEmpty()) {
            WsValidationException validationException = new WsValidationException(errors, wsDomainEntity);
            throw validationException;
        }

        Set<ConstraintViolation<DomainEntity>> violations = this.validator.validate(domainEntity);
        errors.addAll(violations);
        if (!errors.isEmpty()) {
            WsValidationException validationException = new WsValidationException(errors, wsDomainEntity);
            throw validationException;
        }

    }
}
