package com.osmanager.dto;

import jakarta.validation.constraints.*;

/** Request DTO for creating a technician — no id, createdAt or active (system-managed). */
public class TecnicoRequestDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
    private String name;

    @Email(message = "Email must be a valid address")
    @Size(max = 100, message = "Email must be at most 100 characters")
    private String email;

    @Size(max = 20, message = "Phone must be at most 20 characters")
    private String phone;

    @Pattern(
        regexp = "^(\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2})?$",
        message = "CPF must be in format 000.000.000-00 or 00000000000"
    )
    private String cpf;

    @Size(max = 100, message = "Specialty must be at most 100 characters")
    private String specialty;

    @Size(max = 50, message = "Professional license must be at most 50 characters")
    private String professionalLicense;

    // ---- Constructors ----

    public TecnicoRequestDTO() {}

    // ---- Getters and Setters ----

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getProfessionalLicense() { return professionalLicense; }
    public void setProfessionalLicense(String professionalLicense) {
        this.professionalLicense = professionalLicense;
    }
}
