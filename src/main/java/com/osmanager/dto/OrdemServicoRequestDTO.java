package com.osmanager.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Request DTO for creating or updating a Service Order.
 *
 * The client sends JSON like:
 * {
 *   "clientId": 5,
 *   "tecnicoId": 2,
 *   "description": "Engine oil change",
 *   "totalAmount": 150.00,
 *   "discount": 10.00
 * }
 *
 * The controller receives this and passes to the service,
 * which fetches the actual Client and Tecnico entities by ID.
 * We NEVER send full entity objects in request bodies — IDs only.
 */
public class OrdemServicoRequestDTO {

    @NotNull(message = "Client ID is required")
    @Positive(message = "Client ID must be a positive number")
    private Long clientId;

    @NotNull(message = "Technician ID is required")
    @Positive(message = "Technician ID must be a positive number")
    private Long tecnicoId;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    @Size(max = 1000, message = "Notes must be at most 1000 characters")
    private String notes;

    // @DecimalMin: BigDecimal minimum value as a string
    @DecimalMin(value = "0.00", message = "Total amount must be zero or positive")
    @Digits(integer = 8, fraction = 2, message = "Invalid amount format")
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", message = "Discount must be zero or positive")
    @Digits(integer = 8, fraction = 2, message = "Invalid discount format")
    private BigDecimal discount = BigDecimal.ZERO;

    // ---- Constructors ----

    public OrdemServicoRequestDTO() {}

    public OrdemServicoRequestDTO(Long clientId, Long tecnicoId, String description) {
        this.clientId = clientId;
        this.tecnicoId = tecnicoId;
        this.description = description;
    }

    // ---- Getters and Setters ----

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public Long getTecnicoId() { return tecnicoId; }
    public void setTecnicoId(Long tecnicoId) { this.tecnicoId = tecnicoId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
}
