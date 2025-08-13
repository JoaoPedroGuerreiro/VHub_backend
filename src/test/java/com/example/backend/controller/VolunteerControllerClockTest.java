package com.example.backend.controller;

import com.example.backend.mapper.VolunteerMapper;
import com.example.backend.repository.VolunteerRepo;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(VolunteerController.class)
@Import({VolunteerMapper.class, VolunteerControllerClockTest.FixedClockConfig.class})
public class VolunteerControllerClockTest {

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

    @Test
    void rejectsUnderage_leapDayEdge() throws Exception {

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
}
