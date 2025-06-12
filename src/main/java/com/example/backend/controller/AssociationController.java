package com.example.backend.controller;

import com.example.backend.model.Associations;
import com.example.backend.repository.AssociationsRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/associations")
@CrossOrigin(origins = "*")
public class AssociationController {

    @Autowired
    private AssociationsRepo associationsRepo;

    @GetMapping
    public List<Associations> getAllAssociations(){
        return associationsRepo.findAll();
    }

    @PostMapping
    public ResponseEntity<Associations> createAssociation(@RequestBody @Valid Associations associations) {
        Associations saved = associationsRepo.save(associations);
        return ResponseEntity.ok(saved);
    }
}
