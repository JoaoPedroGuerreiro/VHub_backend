package com.example.backend.controller;

import com.example.backend.dto.AssociationRequest;
import com.example.backend.dto.AssociationResponse;
import com.example.backend.mapper.AssociationMapper;
import com.example.backend.model.Associations;
import com.example.backend.repository.AssociationsRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/associations")
@CrossOrigin(origins = "*")
public class AssociationController {

    @Autowired
    private AssociationsRepo associationsRepo;

    @Autowired
    private AssociationMapper associationMapper;

    @GetMapping
    public List<AssociationResponse> getAllAssociations(){

        return associationsRepo.findAll().stream()
                .map(associationMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<AssociationResponse> createAssociation(@RequestBody @Valid AssociationRequest request) {
        Associations entity = associationMapper.toEntity(request);
        Associations saved = associationsRepo.save(entity);
        AssociationResponse response = associationMapper.toDto(saved);
        return ResponseEntity.created(URI.create("/api/associations/" + saved.getId())).body(response);
    }
}
