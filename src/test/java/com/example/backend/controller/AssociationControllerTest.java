package com.example.backend.controller;

import com.example.backend.model.Associations;
import com.example.backend.repository.AssociationsRepo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@WebMvcTest(AssociationController.class)
public class AssociationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssociationsRepo associationsRepo;

    @Test
    void shouldCreateNewAssociation() throws Exception {
        Associations inputAssociation = new Associations();
        inputAssociation.setName("Global Volunteers");
        inputAssociation.setLocation("Portugal");
        inputAssociation.setEventName("For the world");
        inputAssociation.setDescription("Connects volunteers with global projects");
        inputAssociation.setEmail("global@volunteers.com");
        inputAssociation.setStartDate(LocalDate.of(2025, 5, 27));
        inputAssociation.setEndDate(LocalDate.of(2025, 5, 31));

        Associations savedAssociation = new Associations();
        savedAssociation.setId(1L); // Simulate DB-generated ID
        savedAssociation.setName("Global Volunteers");
        savedAssociation.setLocation("Portugal");
        savedAssociation.setEventName("For the world");
        savedAssociation.setDescription("Connects volunteers with global projects");
        savedAssociation.setEmail("global@volunteers.com");
        savedAssociation.setStartDate(LocalDate.of(2025, 5, 27));
        savedAssociation.setEndDate(LocalDate.of(2025, 5, 31));

        Mockito.when(associationsRepo.save(Mockito.any(Associations.class)))
                .thenReturn(savedAssociation);

        String jsonInput = """
                {
                    "name": "Global Volunteers",
                    "location": "Portugal",
                    "eventName": "For the world",
                    "description": "Connects volunteers with global projects",
                    "email": "global@volunteers.com",
                    "startDate": "2025-05-27",
                    "endDate": "2025-05-31"
                }
                """;

        mockMvc.perform(post("/api/associations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Global Volunteers"))
                .andExpect(jsonPath("$.location").value("Portugal"))
                .andExpect(jsonPath("$.startDate").value("2025-05-27"))
                .andExpect(jsonPath("$.endDate").value("2025-05-31"));
    }

    @Test
    void shouldReturnListOfAssociations() throws Exception {
        Associations mock1 = new Associations();
        mock1.setId(1L);
        mock1.setName("Earth Aid");
        mock1.setLocation("Kenya");
        mock1.setEventName("Reforest Kenya");
        mock1.setDescription("Supports reforestation efforts");
        mock1.setEmail("contact@earthaid.org");
        mock1.setStartDate(LocalDate.of(2025, 7, 1));
        mock1.setEndDate(LocalDate.of(2025, 7, 10));

        Associations mock2 = new Associations();
        mock2.setId(1L);
        mock2.setName("World Aid");
        mock2.setLocation("Budapest");
        mock2.setEventName("Clean the earth");
        mock2.setDescription("Supports cleaning efforts");
        mock2.setEmail("contact@worldaid.org");
        mock2.setStartDate(LocalDate.of(2025, 10, 1));
        mock2.setEndDate(LocalDate.of(2025, 10, 10));


        Mockito.when(associationsRepo.findAll()).thenReturn(List.of(mock1, mock2));

        mockMvc.perform(get("/api/associations"))
                .andExpect(status().isOk())

                // mock1
                .andExpect(jsonPath("$[0].name").value("Earth Aid"))
                .andExpect(jsonPath("$[0].location").value("Kenya"))
                .andExpect(jsonPath("$[0].eventName").value("Reforest Kenya"))

                // mock2
                .andExpect(jsonPath("$[1].name").value("World Aid"))
                .andExpect(jsonPath("$[1].location").value("Budapest"))
                .andExpect(jsonPath("$[1].eventName").value("Clean the earth"));
    }

    @Test
    void shouldReturn400WhenRequiredFieldsAreMissing() throws Exception {
        String invalidJson = """
                {
                "name": "",
                "location": "",
                "eventName": "",
                "description": "",
                "email": "not-an-email",
                "startDate": "2025-05-27",
                "endDate": "2025-05-31"
                }
                """;

        mockMvc.perform(
                        post("/api/associations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required."))
                .andExpect(jsonPath("$.location").value("Location is required."))
                .andExpect(jsonPath("$.email").value("Email must be valid."));
    }
}
