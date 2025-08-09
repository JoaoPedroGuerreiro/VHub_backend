package com.example.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class AdultValidator implements ConstraintValidator<Adult, LocalDate> {

    private final Clock clock;

    //Keeping this "no-arguments" constructor is important since Bean Validation may instantiate the validator reflectively.
    public AdultValidator() {

        this.clock = Clock.systemDefaultZone();
    }

    public AdultValidator(Clock clock) {

        this.clock = clock;
    }


    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) return true;
        return ChronoUnit.YEARS.between(birthDate, LocalDate.now(clock)) >= 18;
    }
}
