package com.example.backend.controller;

import com.example.backend.dto.VolunteerRequest;
import com.example.backend.dto.VolunteerResponse;
import com.example.backend.mapper.VolunteerMapper;
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

    @Autowired
    private VolunteerMapper volunteerMapper;

    @GetMapping
    public List<VolunteerResponse> getAllVolunteers(){
        return volunteerRepo.findAll().stream()
                .map(volunteerMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<VolunteerResponse> createVolunteer(@RequestBody @Valid VolunteerRequest request) {
        Volunteers entity = volunteerMapper.toEntity(request);
        Volunteers saved = volunteerRepo.save(entity);
        VolunteerResponse response = volunteerMapper.toDto(saved);
        return ResponseEntity.created(URI.create("/api/volunteers/" + saved.getId()))
                .body(response);
    }
}
