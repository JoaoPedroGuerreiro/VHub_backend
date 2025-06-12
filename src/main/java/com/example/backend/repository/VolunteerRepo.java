package com.example.backend.repository;

import com.example.backend.model.Volunteers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerRepo extends JpaRepository<Volunteers, Long> {

}
