package com.charlyghislain.plancul.validation;

import com.charlyghislain.plancul.domain.Culture;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class CultureharvestDatesValidator implements ConstraintValidator<ValidHarvestDates, Culture> {

    @Override
    public void initialize(ValidHarvestDates validGerminationDate) {
    }

    @Override
    public boolean isValid(Culture culture, ConstraintValidatorContext constraintValidatorContext) {
        if (culture == null) {
            return true;
        }
        LocalDate sowingDate = culture.getSowingDate();
        LocalDate germinationDate = culture.getGerminationDate();
        LocalDate firstHarvestDate = culture.getFirstHarvestDate();
        LocalDate lastHarvestDate = culture.getLastHarvestDate();

        return firstHarvestDate.isAfter(germinationDate)
                && firstHarvestDate.isAfter(sowingDate)
                && firstHarvestDate.isBefore(lastHarvestDate);
    }
}
