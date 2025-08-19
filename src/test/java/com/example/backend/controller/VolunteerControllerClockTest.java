package com.example.backend.controller;

import com.example.backend.mapper.VolunteerMapper;
import com.example.backend.model.Volunteers; // adjust if your entity path differs
import com.example.backend.repository.VolunteerRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller-level test that proves:
 *  - Bean Validation uses your AdultValidator with an injected Clock.
 *  - At the API boundary, a leap-day DOB is rejected on 2026-02-28 (non-leap year) → 400.
 *  - A valid adult payload results in success and hits the repository.
 */

@WebMvcTest(VolunteerController.class)
@Import({ VolunteerMapper.class, VolunteerControllerClockTest.FixedClockConfig.class })
class VolunteerControllerClockTest {

    /**
     * Test-only Clock bean that overrides the production Clock for THIS test slice.
     * Freezes "today" to 2026-02-28T12:00Z so we can assert the leap-day edge deterministically.
     *
     * Spring’s validator factory will prefer the AdultValidator(Clock) constructor and inject this.
     */
    @TestConfiguration
    static class FixedClockConfig {
        @Bean
        Clock clock() {
            return Clock.fixed(Instant.parse("2026-02-28T12:00:00Z"), ZoneOffset.UTC);
        }
    }

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    VolunteerRepo volunteerRepo;

    // ---------------------------
    // Negative edge: underage → 400
    // ---------------------------
    @Test
    void rejectsUnderage_leapDayEdge() throws Exception {
        // NOTE: we don't need to stub(to define fake behaviour for a mock) repo here; validation fails before hitting persistence.
        String json = """
          {
            "name": "Kid Leap",
            "email": "kid@example.com",
            "phone": "123456789",
            "country": "PT",
            "region": "Lisbon",
            "birthDate": "2008-02-29",
            "available": true
          }
        """;

        mockMvc.perform(post("/api/volunteers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.birthDate").value("Must be at least 18 years old"));
    }

    // --------------------------------
    // Positive path: adult → 201 or 200
    // --------------------------------
    @Test
    void acceptsAdult_andPersists() throws Exception {

        // With the fixed date 2026-02-28, someone born on/before 2008-02-28 is >= 18.
        String json = """
          {
            "name": "Exactly Eighteen Today",
            "email": "ok@example.com",
            "phone": "999888777",
            "country": "PT",
            "region": "Lisbon",
            "birthDate": "2008-02-28",
            "available": true
          }
        """;

        // Stub repository save(...) to return an entity with an id (so the controller can echo it back).
        when(volunteerRepo.save(any(Volunteers.class))).thenAnswer(invocation -> {
            Volunteers v = invocation.getArgument(0);
            // give it an ID as if the DB persisted it
            v.setId(123L);
            return v;
        });

        mockMvc.perform(post("/api/volunteers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(result -> {
                    int s = result.getResponse().getStatus();
                    if (s != 200 && s != 201) {
                        throw new AssertionError("Expected 200 or 201 but was " + s);
                    }
                })
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Exactly Eighteen Today"))
                .andExpect(jsonPath("$.email").value("ok@example.com"))
                .andExpect(jsonPath("$.birthDate").value("2008-02-28"))
                .andExpect(jsonPath("$.available").value(true));
    }
}
