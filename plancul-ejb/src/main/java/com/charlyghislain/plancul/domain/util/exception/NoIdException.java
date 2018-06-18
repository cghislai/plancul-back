package com.charlyghislain.plancul.domain.util.exception;

import org.apache.http.HttpStatus;

public class NoIdException extends PlanCulRuntimeException {

    public NoIdException() {
        super(HttpStatus.SC_BAD_REQUEST);
    }
}
