package com.charlyghislain.plancul.util.exception;

import com.charlyghislain.plancul.domain.api.util.WsDomainEntity;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import java.util.Set;

public class WsValidationException extends WsException {

    private final Set<ConstraintViolation<?>> errors;
    private WsDomainEntity wsDomainEntity;

    public WsValidationException(Set<ConstraintViolation<?>> errors, WsDomainEntity wsDomainEntity) {
        super(Response.Status.NOT_ACCEPTABLE);
        this.errors = errors;
        this.wsDomainEntity = wsDomainEntity;
    }

    public Set<ConstraintViolation<?>> getErrors() {
        return errors;
    }

    public WsDomainEntity getWsDomainEntity() {
        return wsDomainEntity;
    }
}
