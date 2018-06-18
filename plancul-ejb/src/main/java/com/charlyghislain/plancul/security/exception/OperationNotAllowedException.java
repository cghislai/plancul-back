package com.charlyghislain.plancul.security.exception;

import com.charlyghislain.plancul.domain.util.exception.PlanCulRuntimeException;
import org.apache.http.HttpStatus;

public class OperationNotAllowedException extends PlanCulRuntimeException {

    public OperationNotAllowedException() {
        super("Operation not allowed", HttpStatus.SC_FORBIDDEN);
    }

}
