package com.charlyghislain.plancul.util;

import com.charlyghislain.plancul.api.domain.util.WsDomainEntity;
import com.charlyghislain.plancul.domain.exception.ValidationViolation;
import com.charlyghislain.plancul.domain.util.DomainEntity;
import com.charlyghislain.plancul.util.exception.WsValidationException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class EntityValidator {

    @Inject
    private Validator validator;

    public void validate(WsDomainEntity wsDomainEntity, DomainEntity domainEntity) {

        Set<ConstraintViolation<WsDomainEntity>> wsEntiryViolations = this.validator.validate(wsDomainEntity);
        List<ValidationViolation> wsEntityErrors = wsEntiryViolations.stream()
                .map(v -> new ValidationViolation(v.getPropertyPath().toString(), v.getMessage()))
                .collect(Collectors.toList());

        if (!wsEntityErrors.isEmpty()) {
            WsValidationException validationException = new WsValidationException(wsEntityErrors, wsDomainEntity);
            throw validationException;
        }

        Set<ConstraintViolation<DomainEntity>> entityViolations = this.validator.validate(domainEntity);
        List<ValidationViolation> entityErrors = entityViolations.stream()
                .map(v -> new ValidationViolation(v.getPropertyPath().toString(), v.getMessage()))
                .collect(Collectors.toList());

        if (!entityErrors.isEmpty()) {
            WsValidationException validationException = new WsValidationException(entityErrors, wsDomainEntity);
            throw validationException;
        }

    }
}
