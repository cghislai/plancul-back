package com.charlyghislain.plancul.domain.validation;

import com.charlyghislain.plancul.domain.Culture;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CultureHarvestDurationsValidator implements ConstraintValidator<ValidHarvestDurations, Culture> {

    @Override
    public void initialize(ValidHarvestDurations validGerminationDate) {
    }

    @Override
    public boolean isValid(Culture culture, ConstraintValidatorContext constraintValidatorContext) {
        if (culture == null) {
            return true;
        }
        int daysUntilGermination = culture.getDaysUntilGermination();
        int daysUntilFirstHarvest = culture.getDaysUntilFirstHarvest();
        if (daysUntilFirstHarvest <= daysUntilGermination) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("must be more than germination duration")
                    .addPropertyNode("daysUntilFirstHarvest")
                    .addConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("must be less than days until first harvest")
                    .addPropertyNode("daysUntilGermination")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
