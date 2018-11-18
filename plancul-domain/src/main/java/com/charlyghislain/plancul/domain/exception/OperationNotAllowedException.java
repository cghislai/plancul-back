package com.charlyghislain.plancul.domain.exception;

public class OperationNotAllowedException extends PlanCulException {
    public OperationNotAllowedException() {
    }

    public OperationNotAllowedException(String message) {
        super(message);
    }

    public OperationNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }

    public OperationNotAllowedException(Throwable cause) {
        super(cause);
    }
}
