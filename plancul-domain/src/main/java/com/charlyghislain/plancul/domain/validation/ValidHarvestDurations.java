package com.charlyghislain.plancul.domain.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {CultureHarvestDurationsValidator.class})
@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface ValidHarvestDurations {
    String message() default "com.charlyghislain.plancul.validation.ValidHarvestDurations.message";
    String daysUntilFirstHarvestMessage() default "com.charlyghislain.plancul.validation.ValidHarvestDurations.daysUntilFirstHarvest.message";
    String daysUntilGerminationMessage() default "com.charlyghislain.plancul.validation.ValidHarvestDurations.daysUntilGermination.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
