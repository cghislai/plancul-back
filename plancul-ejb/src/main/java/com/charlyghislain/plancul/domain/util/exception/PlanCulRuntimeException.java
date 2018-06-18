package com.charlyghislain.plancul.domain.util.exception;

import java.util.Optional;

public class PlanCulRuntimeException extends RuntimeException {

    private Integer httpStatusCode;

    public PlanCulRuntimeException() {
    }

    public PlanCulRuntimeException(String message) {
        super(message);
    }

    public PlanCulRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlanCulRuntimeException(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public PlanCulRuntimeException(String message, Integer httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public PlanCulRuntimeException(String message, Throwable cause, Integer httpStatusCode) {
        super(message, cause);
        this.httpStatusCode = httpStatusCode;
    }

    public Optional<Integer> getHttpStatusCode() {
        return Optional.ofNullable(httpStatusCode);
    }
}
