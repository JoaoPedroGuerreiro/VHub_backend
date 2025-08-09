package com.example.backend.mapper;

import com.example.backend.dto.AssociationRequest;
import com.example.backend.dto.AssociationResponse;
import com.example.backend.model.Associations;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class AssociationsMapperTester {

    private final AssociationMapper mapper = new AssociationMapper();

    @Test
    void testToEntityMapping() {
        AssociationRequest request = new AssociationRequest();
        request.setName("Helping Hands");
        request.setLocation("Lisbon");
        request.setEventName("Green Earth");
        request.setDescription("Environmental initiative");
        request.setEmail("info@helpinghands.org");
        request.setStartDate(LocalDate.of(2025, 6, 1));
        request.setEndDate(LocalDate.of(2025, 6, 10));

        Associations entity = mapper.toEntity(request);

        assertEquals("Helping Hands", entity.getName());
        assertEquals("Lisbon", entity.getLocation());
        assertEquals("Green Earth", entity.getEventName());
        assertEquals("Environmental initiative", entity.getDescription());
        assertEquals("info@helpinghands.org", entity.getEmail());
        assertEquals(LocalDate.of(2025, 6, 1), entity.getStartDate());
        assertEquals(LocalDate.of(2025, 6, 10), entity.getEndDate());
    }

    @Test
    void testToDtoMapping() {
        Associations entity = new Associations();
        entity.setId(100L);
        entity.setName("Global Reach");
        entity.setLocation("Berlin");
        entity.setEventName("Tech for Good");
        entity.setDescription("Technology volunteering");
        entity.setEmail("contact@globalreach.org");
        entity.setStartDate(LocalDate.of(2025, 9, 1));
        entity.setEndDate(LocalDate.of(2025, 9, 5));

        AssociationResponse response = mapper.toDto(entity);

        assertEquals(100L, response.getId());
        assertEquals("Global Reach", response.getName());
        assertEquals("Berlin", response.getLocation());
        assertEquals("Tech for Good", response.getEventName());
        assertEquals("Technology volunteering", response.getDescription());
        assertEquals("contact@globalreach.org", response.getEmail());
        assertEquals(LocalDate.of(2025, 9, 1), response.getStartDate());
        assertEquals(LocalDate.of(2025, 9, 5), response.getEndDate());
    }
}
