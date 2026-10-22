package com.osmanager.dto;

import com.osmanager.model.Tecnico;

import java.time.LocalDateTime;

/** Response DTO for Tecnico — what the API returns to callers. */
public class TecnicoResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String cpf;
    private String specialty;
    private String professionalLicense;
    private Boolean active;
    private LocalDateTime createdAt;
    private String role;

    public static TecnicoResponseDTO from(Tecnico tecnico) {
        TecnicoResponseDTO dto = new TecnicoResponseDTO();
        dto.id                  = tecnico.getId();
        dto.name                = tecnico.getName();
        dto.email               = tecnico.getEmail();
        dto.phone               = tecnico.getPhone();
        dto.cpf                 = tecnico.getCpf();
        dto.specialty           = tecnico.getSpecialty();
        dto.professionalLicense = tecnico.getProfessionalLicense();
        dto.active              = tecnico.getActive();
        dto.createdAt           = tecnico.getCreatedAt();
        dto.role                = tecnico.getRole();  // polymorphic call → "TECHNICIAN"
        return dto;
    }

    // ---- Constructors ----

    public TecnicoResponseDTO() {}

    // ---- Getters ----

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getCpf() { return cpf; }
    public String getSpecialty() { return specialty; }
    public String getProfessionalLicense() { return professionalLicense; }
    public Boolean getActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getRole() { return role; }
}
