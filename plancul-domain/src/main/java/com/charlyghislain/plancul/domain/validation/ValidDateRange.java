package com.charlyghislain.plancul.domain.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = {DateRangeValidator.class})
@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface ValidDateRange {

    String message() default "com.charlyghislain.plancul.validation.ValidDateRange.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean allowEmpty() default false;
}
