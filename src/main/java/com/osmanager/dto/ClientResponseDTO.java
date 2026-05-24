package com.osmanager.dto;

import com.osmanager.model.Client;

import java.time.LocalDateTime;

/**
 * Response DTO for Client — what the API returns to callers.
 *
 * Note the differences from ClientRequestDTO:
 *   + id (set by the DB — not in request)
 *   + createdAt (set by @PrePersist — not in request)
 *   + active (managed by the system — not in request)
 *   - No validation annotations (response is not validated, only serialised)
 *
 * MAPPING NOTE: The service layer converts Client entity → ClientResponseDTO.
 * This is done manually here (no MapStruct) so the mapping is explicit and
 * easy to understand during interviews. In production, consider MapStruct
 * for type-safe, boilerplate-free mapping.
 */
public class ClientResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String cpf;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private Boolean active;
    private LocalDateTime createdAt;
    private String role;   // demonstrates polymorphism — comes from entity.getRole()

    // ---- Static factory method — preferred over constructor ----
    // Naming convention: "from" signals this converts FROM another type.
    // Makes the caller's intent clear: ClientResponseDTO.from(client)

    /**
     * Converts a Client entity to a response DTO.
     * All entity-to-DTO mapping is centralised HERE — not in the controller.
     */
    public static ClientResponseDTO from(Client client) {
        ClientResponseDTO dto = new ClientResponseDTO();
        dto.id        = client.getId();
        dto.name      = client.getName();
        dto.email     = client.getEmail();
        dto.phone     = client.getPhone();
        dto.cpf       = client.getCpf();
        dto.address   = client.getAddress();
        dto.city      = client.getCity();
        dto.state     = client.getState();
        dto.zipCode   = client.getZipCode();
        dto.active    = client.getActive();
        dto.createdAt = client.getCreatedAt();
        dto.role      = client.getRole();  // polymorphic call → "CLIENT"
        return dto;
    }

    // ---- Constructors ----

    public ClientResponseDTO() {}

    // ---- Getters ----
    // Response DTOs are typically read-only (no setters needed by callers)

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getCpf() { return cpf; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZipCode() { return zipCode; }
    public Boolean getActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getRole() { return role; }

    // Setters needed for Jackson deserialization in tests
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setAddress(String address) { this.address = address; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public void setActive(Boolean active) { this.active = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setRole(String role) { this.role = role; }
}
