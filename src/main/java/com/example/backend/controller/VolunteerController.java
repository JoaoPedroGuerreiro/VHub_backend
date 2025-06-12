package com.example.backend.controller;

import com.example.backend.dto.VolunteerRequest;
import com.example.backend.dto.VolunteerResponse;
import com.example.backend.model.Volunteers;
import com.example.backend.repository.VolunteerRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/volunteers")
@CrossOrigin(origins = "*")
public class VolunteerController {

    @Autowired
    private VolunteerRepo volunteerRepo;

    @GetMapping
    public List<VolunteerResponse> getAllVolunteers(){
        return volunteerRepo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<VolunteerResponse> createVolunteer(@RequestBody @Valid VolunteerRequest request) {
        Volunteers entity = toEntity(request);
        Volunteers saved = volunteerRepo.save(entity);
        VolunteerResponse response = toDto(saved);
        return ResponseEntity.created(URI.create("/api/volunteers/" + saved.getId())).body(response);
    }

    private VolunteerResponse toDto(Volunteers v) {
        VolunteerResponse dto = new VolunteerResponse();
        dto.setId(v.getId());
        dto.setName(v.getName());
        dto.setEmail(v.getEmail());
        dto.setPhone(v.getPhone());
        dto.setCountry(v.getCountry());
        dto.setRegion(v.getRegion());
        dto.setBirthDate(v.getBirthDate());
        return dto;
    }

    private Volunteers toEntity(VolunteerRequest dto) {
        Volunteers v = new Volunteers();
        v.setName(dto.getName());
        v.setEmail(dto.getEmail());
        v.setPhone(dto.getPhone());
        v.setCountry(dto.getCountry());
        v.setRegion(dto.getRegion());
        v.setBirthDate(dto.getBirthDate());
        return v;
    }
}
