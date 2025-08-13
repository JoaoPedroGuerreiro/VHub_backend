package com.example.backend.controller;

import com.example.backend.mapper.VolunteerMapper;
import com.example.backend.model.Volunteers;
import com.example.backend.repository.VolunteerRepo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;

@WebMvcTest(VolunteerController.class)
@Import(VolunteerMapper.class)
public class VolunteerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VolunteerRepo volunteerRepo;

    @Test
    void shouldReturnListOfVolunteers() throws Exception {
        Volunteers mockVolunteer = new Volunteers();
        mockVolunteer.setId(1L);
        mockVolunteer.setName("Test Name");
        mockVolunteer.setEmail("email@test.com");
        mockVolunteer.setPhone("913198002");
        mockVolunteer.setCountry("Country Test");
        mockVolunteer.setRegion("Region Test");
        mockVolunteer.setBirthDate(LocalDate.of(1995, 1, 1));
        mockVolunteer.setAvailable(true);

        Mockito.when(volunteerRepo.findAll()).thenReturn(List.of(mockVolunteer));

        mockMvc.perform(get("/api/volunteers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Name"))
                .andExpect(jsonPath("$[0].email").value("email@test.com"));
    }

    @Test
    void shouldCreateNewVolunteer() throws Exception {
        Volunteers savedVolunteer = new Volunteers();
        savedVolunteer.setId(1L);
        savedVolunteer.setName("Maria Example");
        savedVolunteer.setEmail("maria@example.com");
        savedVolunteer.setPhone("913456789");
        savedVolunteer.setCountry("Portugal");
        savedVolunteer.setRegion("Lisbon");
        savedVolunteer.setBirthDate(LocalDate.of(2000, 1, 1));
        savedVolunteer.setAvailable(true);

        Mockito.when(volunteerRepo.save(Mockito.any(Volunteers.class))).thenReturn(savedVolunteer);

        String jsonInput = """
                 {
                 "name": "Maria Example",
                 "email": "maria@example.com",
                 "phone": "913456789",
                 "country": "Portugal",
                 "region": "Lisbon",
                 "birthDate": "2000-01-01",
                 "available": true
                 }
                """;

        mockMvc.perform(post("/api/volunteers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Maria Example"))
                .andExpect(jsonPath("$.email").value("maria@example.com"));
    }

    @Test
    void shouldReturn400WhenRequiredFieldsAreMissing() throws Exception {
        String invalidJson = """
                {
                "name": "",
                "email": "not-an-email",
                "phone": "",
                "country": "",
                "region": "",
                "birthDate": "2020-01-01",
                "available": true
                }
                """;

        mockMvc.perform(
                        post("/api/volunteers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required."))
                .andExpect(jsonPath("$.email").value("Email must be valid."))
                .andExpect(jsonPath("$.phone").value("Phone number is required."))
                .andExpect(jsonPath("$.country").value("Country is required."))
                .andExpect(jsonPath("$.region").value("Region is required."))
                .andExpect(jsonPath("$.birthDate").value("Must be at least 18 years old"));
    }
}
