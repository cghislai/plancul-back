package com.charlyghislain.plancul.domain.exception;

public class PlanCulRuntimeException extends RuntimeException {
    public PlanCulRuntimeException() {
    }

    public PlanCulRuntimeException(String message) {
        super(message);
    }

    public PlanCulRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlanCulRuntimeException(Throwable cause) {
        super(cause);
    }

    public PlanCulRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
