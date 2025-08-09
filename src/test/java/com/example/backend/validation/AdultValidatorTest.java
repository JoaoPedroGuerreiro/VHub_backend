package com.example.backend.validation;

/*
Imports:
- Marks test methods (JUnit 5 annotation): we mark *methods*, not the whole class.
- Static imports for assertTrue/assertFalse so assertions read like English.
- Java Time API: LocalDate, Clock, ZoneId, ZonedDateTime, Instant for time control in tests.
- @BeforeEach: shared setup that runs before *every* test.
*/

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.*;

import org.junit.jupiter.api.BeforeEach;

/**
 * Intentionally mixed styles for learning:
 *
 *  1) Tests WITHOUT @BeforeEach (manual setup per test)
 *     - Shows the “basic” way before learning lifecycle hooks.
 *
 *  2) Tests WITH @BeforeEach (shared setup)
 *     - Removes repetition; every test starts from the same baseline.
 *
 *  3) Special-case tests with a fixed Clock
 *     - Ignore @BeforeEach and inject a custom time source to make
 *       leap-year boundary behavior deterministic.
 */

public class AdultValidatorTest {

    // --- Shared fields used by @BeforeEach section ---
    // Not 'final' because they are assigned in setUp().

    AdultValidator validator;
    LocalDate today;

    /*
     * JUnit 5 note:
     * - Methods annotated with @BeforeEach/@Test do NOT need to be public.
     * - The engine uses reflection; package-private is perfectly fine.
     */
    @BeforeEach
    void setUp() {
        validator = new AdultValidator(); // uses system clock (production behaviour).
        today = LocalDate.now(); // snapshot of "today" for simple tests.
    }

    // ---------------------------------------------------------------------
    // 1) Tests WITHOUT @BeforeEach (manual setup per test)
    //    These show the basic approach because at this time I hadn't learned lifecycle hooks yet.
    // ---------------------------------------------------------------------

    @Test
    public void exactly18YearsOld_shouldBeValid() {

        //Arrange (manual)
        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(18);

        //Act (manual instance)
        AdultValidator adultValidator = new AdultValidator();
        boolean result = adultValidator.isValid(dob, null);

        //Assert
        assertTrue(result);
    }

    @Test
    public void oneDayShyOf18_shouldBeInvalid() {

        //Arrange
        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(18).plusDays(1);

        //Act
        AdultValidator adultValidator = new AdultValidator();
        boolean result = adultValidator.isValid(dob, null);

        //Assert
        assertFalse(result);
    }

    @Test
    public void oneDayAfter18_shouldBeValid() {

        //Arrange
        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(18).minusDays(1);

        //Act
        AdultValidator adultValidator = new AdultValidator();
        boolean result = adultValidator.isValid(dob, null);

        //Assert
        assertTrue(result);
    }

    @Test
    public void clearlyUnderage_shouldBeInvalid() {

        //Arrange
        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(8);

        //Act
        AdultValidator adultValidator = new AdultValidator();
        boolean result = adultValidator.isValid(dob, null);

        //Assert
        assertFalse(result);
    }

    @Test
    public void clearlyAdult_shouldBeValid() {

        //Arrange
        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(25);

        //Act
        AdultValidator adultValidator = new AdultValidator();
        boolean result = adultValidator.isValid(dob, null);

        //Assert
        assertTrue(result);
    }

    @Test
    public void nullBirthDate_shouldBeValid() {

        //Arrange: @Adult is null-tolerant by design; presence is enforced by @NotNull at DTO level.
        LocalDate dob = null;

        //Act
        AdultValidator adultValidator = new AdultValidator();
        boolean result = adultValidator.isValid(dob, null);

        //Assert
        assertTrue(result);

    }

    // ---------------------------------------------------------------------
    // 2) Tests WITH @BeforeEach (shared setup)
    //    These use the 'validator' and 'today' fields initialized in setUp().
    // ---------------------------------------------------------------------


    @Test
    public void futureBirthDate_shouldBeInvalid() {

        //Arrange: using shared 'today' field from @BeforeEach.
        LocalDate dob = today.plusDays(1);

        //Act
        boolean result = validator.isValid(dob, null);

        //Assert
        assertFalse(result);
    }

    // ---------------------------------------------------------------------
    // 3) Special-case tests with a fixed Clock
    //    These ignore @BeforeEach and build a validator bound to a fixed "today".
    //    Policy chosen: Option B → Feb 29 birthdays become adult on Mar 1 in non-leap years.
    // ---------------------------------------------------------------------

    @Test
    public void leapYearDob_Feb28NonLeapYear_shouldBeInvalid() {

        //Freeze today (non-leap year) a day 'BEFORE' as is the majority convention worldwide approach: should a pearson birthday be after or before its day.
        LocalDate fixedToday = LocalDate.of(2026, 2, 28);
        ZonedDateTime zdt = fixedToday.atStartOfDay(ZoneId.systemDefault());
        Instant instant = zdt.toInstant();
        Clock clock = Clock.fixed(instant, ZoneId.systemDefault());
        AdultValidator validatorWithFixedClock = new AdultValidator(clock);

        //Arrange DOB (born on leap day in 2008)
        LocalDate dob = LocalDate.of(2008, 2, 29);

        //Act
        boolean result = validatorWithFixedClock.isValid(dob, null);

        //Assert: not yet adult on Feb 28.
        assertFalse(result);
    }

    @Test
    public void leapYearDob_Mar01NonLeapYear_shouldBeValid() {

        //Freeze today
        LocalDate fixedToday = LocalDate.of(2026, 3, 1);
        ZonedDateTime zdt = fixedToday.atStartOfDay(ZoneId.systemDefault());
        Instant instant = zdt.toInstant();
        Clock clock = Clock.fixed(instant, ZoneId.systemDefault());
        AdultValidator validatorWithFixedClock = new AdultValidator(clock);

        //Arrange DOB (born on leap day in 2008).
        LocalDate dob = LocalDate.of(2008, 2, 29);

        //Act
        boolean result = validatorWithFixedClock.isValid(dob, null);

        //Assert: becomes adult on March 1st.
        assertTrue(result);
    }
}
