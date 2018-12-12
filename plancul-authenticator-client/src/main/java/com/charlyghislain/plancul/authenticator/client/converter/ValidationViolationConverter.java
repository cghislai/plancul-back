package com.charlyghislain.plancul.authenticator.client.converter;

import com.charlyghislain.authenticator.management.api.domain.WsValidationError;
import com.charlyghislain.authenticator.management.api.domain.WsViolationError;
import com.charlyghislain.plancul.domain.exception.ValidationViolation;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ValidationViolationConverter {

    public ValidationViolation toValidationViolation(WsViolationError violationError) {
        ValidationViolation validationViolation = new ValidationViolation(violationError.getFieldName(), violationError.getMessage());
        return validationViolation;
    }
}
