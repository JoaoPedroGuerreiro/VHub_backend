package com.example.backend.validation;

/*
This file intentionally shows both:
- A clean, deterministic suite using a fixed UTC Clock (the “automatic” setup).
- A few demo tests that use the real system time, tagged so you can skip them in CI.
*/

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AdultValidator tests
 *
 * Deterministic rule under test:
 *   ChronoUnit.YEARS.between(birthDate, today) >= 18
 *
 * We freeze "today" with a fixed UTC Clock so results are deterministic forever.
 */
public class AdultValidatorTest {

    // ---------- Fixed clock (automatic setup for deterministic tests) ----------
    private Clock fixed;
    private LocalDate today;
    private AdultValidator validator;

    /** Always use UTC in tests to avoid TZ surprises across machines/CI. */
    private static Clock fixedAt(LocalDate dayUtc) {
        return Clock.fixed(dayUtc.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);
    }

    @BeforeEach
    void setUp() {
        // Pretend "today" = 2030-01-01 UTC for all deterministic tests
        fixed = fixedAt(LocalDate.of(2030, 1, 1));
        today = LocalDate.now(fixed);
        validator = new AdultValidator(fixed);
    }

    // ---------------------------------------------------------------------
    // Deterministic tests (cover all edge cases using the fixed clock)
    // ---------------------------------------------------------------------

    // Clear interior cases
    @Test void clearlyAdult_isValid() {
        assertTrue(validator.isValid(today.minusYears(25), null));
    }

    @Test void clearlyUnderage_isInvalid() {
        assertFalse(validator.isValid(today.minusYears(8), null));
    }

    // Boundaries around 18
    @Test void exactly18_isValid() {
        assertTrue(validator.isValid(today.minusYears(18), null));
    }

    @Test void oneDayShyOf18_isInvalid() {
        assertFalse(validator.isValid(today.minusYears(18).plusDays(1), null));
    }

    @Test void oneDayAfter18_isValid() {
        assertTrue(validator.isValid(today.minusYears(18).minusDays(1), null));
    }

    // Null policy: @Adult is null-tolerant; presence is enforced by @NotNull on DTOs
    @Test void nullBirthDate_isValid_because_presence_is_enforced_elsewhere() {
        assertTrue(validator.isValid(null, null));
    }

    // Future date → invalid
    @Test void birthDateInFuture_isInvalid() {
        assertFalse(validator.isValid(today.plusDays(1), null));
    }

    // Leap-day policy: Feb 29 birthdays become adult on Mar 1 in non-leap years
    // (These use a per-test fixed clock different from the class default.)
    @Test void leapDay_Feb28_nonLeapYear_isInvalid() {
        Clock c = fixedAt(LocalDate.of(2026, 2, 28)); // non-leap year
        AdultValidator v = new AdultValidator(c);
        assertFalse(v.isValid(LocalDate.of(2008, 2, 29), null));
    }

    @Test void leapDay_Mar01_nonLeapYear_isValid() {
        Clock c = fixedAt(LocalDate.of(2026, 3, 1)); // non-leap year
        AdultValidator v = new AdultValidator(c);
        assertTrue(v.isValid(LocalDate.of(2008, 2, 29), null));
    }

    // Tiny extra demo that shows determinism at a birthday edge
    @Test void turns18Tomorrow_fixedClock_demo() {
        Clock c = fixedAt(LocalDate.of(2030, 1, 1));
        AdultValidator v = new AdultValidator(c);
        LocalDate birth = LocalDate.of(2012, 1, 2); // 17y 364d relative to 2030-01-01
        assertFalse(v.isValid(birth, null)); // deterministic forever
    }

    // ---------------------------------------------------------------------
    // This was the first approach I had with this test:
    // Demo tests using the REAL system clock (can break at boundaries).
    // Tagging them with "demo" I can still run everything (the default) or
    // run only the deterministic ones with (mvn test -Dgroups=!demo).
    // ---------------------------------------------------------------------

    @Tag("demo")
    @Test void exactly18YearsOld_shouldBeValid_realNow() {
        LocalDate t = LocalDate.now();
        LocalDate dob = t.minusYears(18);
        assertTrue(new AdultValidator().isValid(dob, null));
    }

    @Tag("demo")
    @Test void oneDayShyOf18_shouldBeInvalid_realNow() {
        LocalDate t = LocalDate.now();
        LocalDate dob = t.minusYears(18).plusDays(1);
        assertFalse(new AdultValidator().isValid(dob, null));
    }

    @Tag("demo")
    @Test void oneDayAfter18_shouldBeValid_realNow() {
        LocalDate t = LocalDate.now();
        LocalDate dob = t.minusYears(18).minusDays(1);
        assertTrue(new AdultValidator().isValid(dob, null));
    }

    @Tag("demo")
    @Test void clearlyUnderage_shouldBeInvalid_realNow() {
        LocalDate t = LocalDate.now();
        LocalDate dob = t.minusYears(8);
        assertFalse(new AdultValidator().isValid(dob, null));
    }

    @Tag("demo")
    @Test void clearlyAdult_shouldBeValid_realNow() {
        LocalDate t = LocalDate.now();
        LocalDate dob = t.minusYears(25);
        assertTrue(new AdultValidator().isValid(dob, null));
    }

    @Tag("demo")
    @Test void nullBirthDate_shouldBeValid_realNow() {
        assertTrue(new AdultValidator().isValid(null, null));
    }
}
