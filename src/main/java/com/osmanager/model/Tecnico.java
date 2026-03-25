package com.osmanager.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entidade JPA: Técnico
 * Representa um técnico da oficina
 */
@Entity
@Table(name = "tecnicos", indexes = {
    @Index(name = "idx_nome_tecnico", columnList = "nome"),
    @Index(name = "idx_email_tecnico", columnList = "email"),
    @Index(name = "idx_cpf_tecnico", columnList = "cpf")
})
public class Tecnico implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", length = 150, nullable = false)
    private String name;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "telefone", length = 20)
    private String phone;

    @Column(name = "cpf", length = 14, unique = true)
    private String cpf;

    @Column(name = "especialidade", length = 100)
    private String specialty;

    @Column(name = "ativo")
    private Boolean active = true;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Construtores
    public Tecnico() {
    }

    public Tecnico(String name, String email, String phone, String cpf, String specialty) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.cpf = cpf;
        this.specialty = specialty;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

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

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
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

    @Override
    public String toString() {
        return "Tecnico{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", cpf='" + cpf + '\'' +
                ", specialty='" + specialty + '\'' +
                '}';
    }
}
