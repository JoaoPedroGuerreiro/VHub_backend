package com.example.backend.mapper;

import com.example.backend.dto.AssociationRequest;
import com.example.backend.dto.AssociationResponse;
import com.example.backend.model.Associations;
import org.springframework.stereotype.Component;

@Component
public class AssociationMapper {

    public AssociationResponse toDto(Associations entity) {
        AssociationResponse dto = new AssociationResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLocation(entity.getLocation());
        dto.setEventName(entity.getEventName());
        dto.setDescription(entity.getDescription());
        dto.setEmail(entity.getEmail());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        return dto;
    }

    public Associations toEntity(AssociationRequest dto) {
        Associations entity = new Associations();
        entity.setName(dto.getName());
        entity.setLocation(dto.getLocation());
        entity.setEventName(dto.getEventName());
        entity.setDescription(dto.getDescription());
        entity.setEmail(dto.getEmail());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        return entity;
    }
}

