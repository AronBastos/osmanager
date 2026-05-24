package com.osmanager.dto;

import jakarta.validation.constraints.*;

/**
 * ==============================================================
 * DTO (Data Transfer Object) — INTERVIEW NOTE (Topics 5 & 6)
 * ==============================================================
 * WHY use DTOs instead of passing entities directly to controllers?
 *
 * 1. SECURITY: Entities may have sensitive fields (e.g., password hash,
 *    internal IDs) that should never be exposed in the API.
 *
 * 2. DECOUPLING: The API contract (DTO shape) can evolve independently
 *    of the database schema (entity shape).
 *
 * 3. VALIDATION: DTOs carry @NotBlank, @Email, etc. — entities don't
 *    need these annotations, keeping them clean JPA-only.
 *
 * 4. N+1 PREVENTION: Returning a DTO from the service prevents Jackson
 *    from lazily triggering association queries via getters.
 *
 * PATTERN: Request DTO (input) ≠ Response DTO (output)
 *   - ClientRequestDTO: what the client SENDS (no id, no createdAt)
 *   - ClientResponseDTO: what the API RETURNS (includes id, createdAt)
 * ==============================================================
 *
 * ==============================================================
 * BEAN VALIDATION — INTERVIEW NOTE (Topic 5)
 * ==============================================================
 * Add spring-boot-starter-validation to pom.xml.
 * Annotate DTO fields with constraints.
 * Add @Valid to @RequestBody in the controller.
 * Spring automatically throws MethodArgumentNotValidException
 * when validation fails — your @ControllerAdvice catches it
 * and returns a structured 400 Bad Request.
 * ==============================================================
 */
public class ClientRequestDTO {

    // @NotBlank: field must not be null, empty (""), or blank ("   ")
    // @Size: sets min/max character length — enforced BEFORE DB truncation
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
    private String name;

    // @Email: validates format (has @, has domain) — not deliverability
    // nullable = true: email is optional for clients
    @Email(message = "Email must be a valid address")
    @Size(max = 100, message = "Email must be at most 100 characters")
    private String email;

    @Size(max = 20, message = "Phone must be at most 20 characters")
    private String phone;

    // @Pattern: validates against a regex — useful for formatted fields
    // CPF Brazilian format: 000.000.000-00 or 00000000000
    @Pattern(
        regexp = "^(\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2})?$",
        message = "CPF must be in format 000.000.000-00 or 00000000000"
    )
    private String cpf;

    @Size(max = 255, message = "Address must be at most 255 characters")
    private String address;

    @Size(max = 100, message = "City must be at most 100 characters")
    private String city;

    @Size(min = 2, max = 2, message = "State must be 2 characters (e.g., SP)")
    private String state;

    @Pattern(
        regexp = "^(\\d{5}-?\\d{3})?$",
        message = "ZIP code must be in format 00000-000 or 00000000"
    )
    private String zipCode;

    // ---- Constructors ----

    public ClientRequestDTO() {}

    public ClientRequestDTO(String name, String email, String phone, String cpf) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.cpf = cpf;
    }

    // ---- Getters and Setters ----

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
}
