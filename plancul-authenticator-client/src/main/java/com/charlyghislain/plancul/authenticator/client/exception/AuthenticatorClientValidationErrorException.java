package com.charlyghislain.plancul.authenticator.client.exception;

import com.charlyghislain.plancul.domain.exception.ValidationViolation;

import java.util.List;

public class AuthenticatorClientValidationErrorException extends AuthenticatorClientError {

    private List<ValidationViolation> violationList;

    public AuthenticatorClientValidationErrorException(List<ValidationViolation> violationList) {
        this.violationList = violationList;
    }

    public List<ValidationViolation> getViolationList() {
        return violationList;
    }

    public void setViolationList(List<ValidationViolation> violationList) {
        this.violationList = violationList;
    }
}
