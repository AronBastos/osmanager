package com.osmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * ==============================================================
 * OOP PILLAR 3 — ABSTRACTION (Topic 1)
 * ==============================================================
 * Person is an ABSTRACT base — it models the concept of "a person
 * in the workshop system" without being instantiable by itself.
 *
 * @MappedSuperclass tells JPA: "Don't create a table for Person.
 * Instead, map Person's fields into the tables of its subclasses."
 *
 * This is DIFFERENT from @Entity + @Inheritance (which creates
 * a shared table or joined tables). @MappedSuperclass just shares
 * field mappings — each subclass gets its own independent table.
 *
 * INTERVIEW NOTE — Interface vs Abstract Class:
 *   - Interface: defines a CONTRACT. No state. Use for "can-do".
 *     Example: Serializable, Comparable — a class CAN do these.
 *   - Abstract Class: partial implementation with state (fields).
 *     Only one parent allowed. Use for "is-a" relationships.
 *     Example: Person — Client IS A Person, Tecnico IS A Person.
 *   - Rule: prefer interfaces for flexibility. Use abstract class
 *     when sharing LOGIC and FIELDS between closely related classes.
 * ==============================================================
 *
 * ==============================================================
 * OOP PILLAR 1 — ENCAPSULATION (Topic 1)
 * ==============================================================
 * All fields are PRIVATE. External code can only read/write them
 * through getter and setter methods. This allows us to:
 *   1. Enforce invariants (e.g., name cannot be blank)
 *   2. Change the internal representation without breaking callers
 *   3. Control which fields are readable vs writable
 * ==============================================================
 */
@MappedSuperclass
public abstract class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // IDENTITY: DB auto-increment. Alternatives: SEQUENCE (PostgreSQL),
    // UUID (distributed systems). IDENTITY is simplest for MySQL.
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must be at most 150 characters")
    @Column(name = "nome", nullable = false, length = 150)
    private String name;

    @Email(message = "Email must be a valid address")
    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "telefone", length = 20)
    private String phone;

    @Column(name = "cpf", length = 14, unique = true)
    private String cpf;

    @Column(name = "ativo")
    private Boolean active = true;

    // updatable = false: this field is set on INSERT and never changed.
    // @PrePersist fills it automatically — see subclasses.
    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ==============================================================
    // OOP PILLAR 4 — POLYMORPHISM (Topic 1)
    // ==============================================================
    // getRole() is an ABSTRACT method — every concrete subclass MUST
    // override it. This is COMPILE-TIME polymorphism enforcement.
    //
    // At runtime:
    //   Person p = new Client();
    //   p.getRole(); // calls Client's implementation → "CLIENT"
    //
    // This is exactly what the study guide means:
    // "The same method name behaves differently depending on the object type."
    // ==============================================================

    /**
     * Returns the role of this person in the system.
     * Subclasses MUST implement this — it's the contract.
     */
    public abstract String getRole();

    // ---- Getters and Setters (Encapsulation) ----

    public Long getId() {
        return id;
    }

    // Note: setId is intentionally NOT provided on some entities to prevent
    // external code from changing a managed entity's identity.
    // For JPA, the ID is set by the DB — we expose it read-only.
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
