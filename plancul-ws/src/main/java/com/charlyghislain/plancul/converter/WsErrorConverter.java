package com.charlyghislain.plancul.converter;

import com.charlyghislain.plancul.api.domain.response.WsContraintViolation;
import com.charlyghislain.plancul.api.domain.response.WsValidationError;
import com.charlyghislain.plancul.api.domain.util.WsDomainEntity;
import com.charlyghislain.plancul.api.domain.util.WsError;
import com.charlyghislain.plancul.domain.exception.ValidationViolation;
import com.charlyghislain.plancul.util.exception.WsValidationException;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class WsErrorConverter {

    public WsError fromThrowable(Throwable exception) {
        String message = exception.getMessage();

        WsError wsError = new WsError();
        wsError.setMessage(message);

        return wsError;
    }

    public WsValidationError fromValidationException(WsValidationException exception) {
        List<WsContraintViolation> violations = exception.getViolations().stream()
                .map(this::fromValidationViolation)
                .collect(Collectors.toList());
        WsDomainEntity wsDomainEntity = exception.getWsDomainEntity();

        WsValidationError validationError = new WsValidationError();
        validationError.setErrors(violations);
        validationError.setEntity(wsDomainEntity);
        return validationError;
    }


    private WsContraintViolation fromValidationViolation(ValidationViolation constraintViolation) {
        String fieldName = constraintViolation.getFieldName();
        String message = constraintViolation.getMessage();
        // TODO i18n

        WsContraintViolation wsContraintViolation = new WsContraintViolation();
        wsContraintViolation.setPropertyPath(fieldName);
        wsContraintViolation.setMessage(message);
        return wsContraintViolation;
    }
}
