package com.charlyghislain.plancul.domain.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    public static final int MIN_PASSWORD_SIZE = 8;

    @Override
    public void initialize(ValidPassword constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        if (value.length() < MIN_PASSWORD_SIZE) {
            context.buildConstraintViolationWithTemplate("Password is too short (8 characers minimum)")
                    .addConstraintViolation();
            return false;
        }
        // TODO: character classes
        return true;
    }
}
