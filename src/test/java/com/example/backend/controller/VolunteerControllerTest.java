package com.example.backend.controller;

import com.example.backend.model.Volunteers;
import com.example.backend.repository.VolunteerRepo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;

@WebMvcTest(VolunteerController.class)
public class VolunteerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
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

        Mockito.when(volunteerRepo.findAll()).thenReturn(List.of(mockVolunteer));

        mockMvc.perform(get("/api/volunteers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Name"));
    }

    @Test
    void shouldCreateNewVolunteer() throws Exception {
        Volunteers inputVolunteer = new Volunteers();
        inputVolunteer.setName("Maria Example");
        inputVolunteer.setEmail("maria@example.com");
        inputVolunteer.setPhone("913456789");
        inputVolunteer.setCountry("Portugal");
        inputVolunteer.setRegion("Lisbon");

        Volunteers savedVolunteer = new Volunteers();
        savedVolunteer.setId(1L);
        savedVolunteer.setName("Maria Example");
        savedVolunteer.setEmail("maria@example.com");
        savedVolunteer.setPhone("913456789");
        savedVolunteer.setCountry("Portugal");
        savedVolunteer.setRegion("Lisbon");

        Mockito.when(volunteerRepo.save(Mockito.any(Volunteers.class))).thenReturn(savedVolunteer);

        String jsonInput = """
                 {
                 "name": "Maria Example",
                 "email": "maria@example.com",
                 "phone": "913456789",
                 "country": "Portugal",
                 "region": "Lisbon" 
                 }
                """;
        mockMvc.perform(post("/api/volunteers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(status().isOk())
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
                "region": ""
                }
                """;

        mockMvc.perform(
                        post("/api/volunteers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required."))
                .andExpect(jsonPath("$.email").value("Email must be valid."))
                .andExpect(jsonPath("$.phone").value("Phone number is required."));
    }
}
