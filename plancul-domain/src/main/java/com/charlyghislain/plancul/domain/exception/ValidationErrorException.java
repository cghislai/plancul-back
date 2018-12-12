package com.charlyghislain.plancul.domain.exception;

import java.util.List;

public class ValidationErrorException extends PlanCulException {
    private List<ValidationViolation> violationList;

    public ValidationErrorException(List<ValidationViolation> violationList) {
        this.violationList = violationList;
    }

    public List<ValidationViolation> getViolationList() {
        return violationList;
    }

    public void setViolationList(List<ValidationViolation> violationList) {
        this.violationList = violationList;
    }
}
