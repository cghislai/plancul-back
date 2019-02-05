package com.charlyghislain.plancul.domain.validation;


import com.charlyghislain.plancul.domain.util.DateRange;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.temporal.ChronoUnit;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, DateRange> {

    private ValidDateRange annotation;

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(DateRange value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        long dayDuration = value.getStart().until(value.getEnd(), ChronoUnit.DAYS);
        if (annotation.allowEmpty()) {
            return dayDuration >= 0;
        } else {
            return dayDuration > 0;
        }
    }
}
