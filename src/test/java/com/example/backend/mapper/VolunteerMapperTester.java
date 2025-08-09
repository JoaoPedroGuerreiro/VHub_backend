package com.example.backend.mapper;

import com.example.backend.dto.VolunteerRequest;
import com.example.backend.dto.VolunteerResponse;
import com.example.backend.model.Volunteers;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class VolunteerMapperTester {

    private final VolunteerMapper mapper = new VolunteerMapper();

    @Test
    void shouldMapEntityToDtoCorrectly(){
        Volunteers volunteer = new Volunteers();
        volunteer.setId(1L);
        volunteer.setName("Alice");
        volunteer.setEmail("alice@example.com");
        volunteer.setPhone("123456789");
        volunteer.setCountry("Wonderland");
        volunteer.setRegion("East");
        volunteer.setBirthDate(LocalDate.of(2000, 1, 1));
        volunteer.setAvailable(true);

        VolunteerResponse dto = mapper.toDto(volunteer);

        assertEquals(1L, dto.getId());
        assertEquals("Alice", dto.getName());
        assertEquals("alice@example.com", dto.getEmail());
        assertEquals("123456789", dto.getPhone());
        assertEquals("Wonderland", dto.getCountry());
        assertEquals("East", dto.getRegion());
        assertEquals(LocalDate.of(2000, 1, 1), dto.getBirthDate());
        assertTrue(dto.isAvailable());
    }

    @Test
    void shouldMapDtoToEntityCorrectly() {
        VolunteerRequest request = new VolunteerRequest();
        request.setName("Bob");
        request.setEmail("bob@example.com");
        request.setPhone("987654321");
        request.setCountry("Utopia");
        request.setRegion("North");
        request.setBirthDate(LocalDate.of(1995, 5, 15));
        request.setAvailable(false);

        Volunteers entity = mapper.toEntity(request);

        assertEquals("Bob", entity.getName());
        assertEquals("bob@example.com", entity.getEmail());
        assertEquals("987654321", entity.getPhone());
        assertEquals("Utopia", entity.getCountry());
        assertEquals("North", entity.getRegion());
        assertEquals(LocalDate.of(1995, 5, 15), entity.getBirthDate());
        assertFalse(entity.isAvailable());
    }
}