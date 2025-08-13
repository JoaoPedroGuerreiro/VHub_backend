package com.example.backend.model;

import com.example.backend.validation.Adult;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
public class Volunteers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Name is required.")
    private String name;

    @NotNull(message = "Birthdate is required.")
    @Adult
    private LocalDate birthdate;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid.")
    private String email;

    @NotBlank(message = "Phone number is required.")
    @Size(min = 9, max = 15, message = "Phone number must be between 9 and 15 characters.")
    private String phone;

    @NotBlank(message = "Country is required.")
    private String country;

    @NotBlank(message = "Region is required.")
    private String region;

    @Column(nullable = false, columnDefinition = "BOOLEAN") //This will make SQL show the column as a 'tinyint(1)' an alias for 'BOOLEAN' and not the default bit(1) to make it easier to read.
    private boolean available;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthdate;
    }

    public void setBirthDate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

}
