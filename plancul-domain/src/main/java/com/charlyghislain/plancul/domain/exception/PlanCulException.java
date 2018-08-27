package com.charlyghislain.plancul.domain.exception;

public class PlanCulException extends Exception {
    public PlanCulException() {
    }

    public PlanCulException(String message) {
        super(message);
    }

    public PlanCulException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlanCulException(Throwable cause) {
        super(cause);
    }

    public PlanCulException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
