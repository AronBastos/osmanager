package com.osmanager.model;

import jakarta.persistence.*;

/**
 * Technician entity — extends Person (Inheritance pillar).
 *
 * A Tecnico IS-A Person — shares id, name, email, phone, cpf,
 * active, and createdAt from the Person @MappedSuperclass.
 *
 * Table: "tecnicos" (separate from "clients" — @MappedSuperclass means
 * each subclass has its own independent table with all fields flattened in.)
 *
 * INTERVIEW NOTE (Topic 6 — JPA Entity):
 * Both Client and Tecnico extend Person, but in the database they are
 * completely separate tables with no shared rows or foreign keys between them.
 * This is the simplest JPA inheritance strategy — @MappedSuperclass.
 */
@Entity
@Table(
    name = "tecnicos",
    indexes = {
        @Index(name = "idx_tecnico_name", columnList = "nome"),
        @Index(name = "idx_tecnico_cpf", columnList = "cpf")
    }
)
public class Tecnico extends Person {

    @Column(name = "especialidade", length = 100)
    private String specialty;

    // CREA or equivalent professional license number
    @Column(name = "registro_profissional", length = 50)
    private String professionalLicense;

    @PrePersist
    protected void prePersist() {
        if (getCreatedAt() == null) {
            setCreatedAt(java.time.LocalDateTime.now());
        }
    }

    /**
     * Polymorphism — Tecnico's own implementation of getRole().
     * Person p = new Tecnico(); p.getRole() → "TECHNICIAN"
     */
    @Override
    public String getRole() {
        return "TECHNICIAN";
    }

    // ---- Constructors ----

    public Tecnico() {}

    public Tecnico(String name, String email, String phone, String cpf, String specialty) {
        setName(name);
        setEmail(email);
        setPhone(phone);
        setCpf(cpf);
        this.specialty = specialty;
    }

    // ---- Getters and Setters ----

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getProfessionalLicense() { return professionalLicense; }
    public void setProfessionalLicense(String professionalLicense) {
        this.professionalLicense = professionalLicense;
    }

    @Override
    public String toString() {
        return "Tecnico{id=" + getId() + ", name='" + getName() + "', specialty='" + specialty + "'}";
    }
}
