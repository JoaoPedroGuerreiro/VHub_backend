package com.example.backend.mapper;

import com.example.backend.dto.VolunteerRequest;
import com.example.backend.dto.VolunteerResponse;
import com.example.backend.model.Volunteers;
import org.springframework.stereotype.Component;

@Component
public class VolunteerMapper {

    public VolunteerResponse toDto(Volunteers v) {
        VolunteerResponse dto = new VolunteerResponse();
        dto.setId(v.getId());
        dto.setName(v.getName());
        dto.setEmail(v.getEmail());
        dto.setPhone(v.getPhone());
        dto.setCountry(v.getCountry());
        dto.setRegion(v.getRegion());
        dto.setBirthDate(v.getBirthDate());
        dto.setAvailable(v.isAvailable());
        return dto;
    }

    public Volunteers toEntity(VolunteerRequest dto) {
        Volunteers v = new Volunteers();
        v.setName(dto.getName());
        v.setEmail(dto.getEmail());
        v.setPhone(dto.getPhone());
        v.setCountry(dto.getCountry());
        v.setRegion(dto.getRegion());
        v.setBirthDate(dto.getBirthDate());
        v.setAvailable(dto.isAvailable());
        return v;
    }
}
