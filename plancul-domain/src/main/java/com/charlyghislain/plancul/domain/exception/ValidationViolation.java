package com.charlyghislain.plancul.domain.exception;

public class ValidationViolation {
    private String fieldName;
    private String message;

    public ValidationViolation(String fieldName, String message) {
        this.fieldName = fieldName;
        this.message = message;
    }

    public ValidationViolation() {
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
