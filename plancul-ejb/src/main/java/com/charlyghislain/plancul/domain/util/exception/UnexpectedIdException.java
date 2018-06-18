package com.charlyghislain.plancul.domain.util.exception;

import org.apache.http.HttpStatus;

public class UnexpectedIdException extends PlanCulRuntimeException {
    public UnexpectedIdException() {
        super(HttpStatus.SC_BAD_REQUEST);
    }
}
