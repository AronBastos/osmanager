package com.osmanager.dto;

import com.osmanager.model.OrdemServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for Service Order — what the API returns.
 *
 * Notice: we embed client and technician as lightweight sub-objects
 * (just id + name), NOT the full entity. This avoids:
 *   1. Exposing unnecessary data (address, CPF, etc.)
 *   2. Triggering lazy loading of nested associations
 *   3. Circular serialization (client → orders → client → ...)
 */
public class OrdemServicoResponseDTO {

    private Long id;
    private Long clientId;
    private String clientName;
    private Long tecnicoId;
    private String tecnicoName;
    private String status;
    private String description;
    private String notes;
    private BigDecimal totalAmount;
    private BigDecimal discount;
    private BigDecimal finalAmount;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;

    /**
     * Static factory — converts entity to DTO.
     * NOTE: client and tecnico must be loaded before calling this
     * (i.e., use @EntityGraph or JOIN FETCH in the repository query).
     * If they are LAZY and not initialised, getClient().getName() will
     * throw LazyInitializationException outside a transaction.
     */
    public static OrdemServicoResponseDTO from(OrdemServico os) {
        OrdemServicoResponseDTO dto = new OrdemServicoResponseDTO();
        dto.id          = os.getId();
        dto.clientId    = os.getClient().getId();
        dto.clientName  = os.getClient().getName();
        dto.tecnicoId   = os.getTecnico().getId();
        dto.tecnicoName = os.getTecnico().getName();
        dto.status      = os.getStatus().name();
        dto.description = os.getDescription();
        dto.notes       = os.getNotes();
        dto.totalAmount = os.getTotalAmount();
        dto.discount    = os.getDiscount();
        dto.finalAmount = os.getFinalAmount();
        dto.openedAt    = os.getOpenedAt();
        dto.closedAt    = os.getClosedAt();
        return dto;
    }

    // ---- Constructors ----

    public OrdemServicoResponseDTO() {}

    // ---- Getters and Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public Long getTecnicoId() { return tecnicoId; }
    public void setTecnicoId(Long tecnicoId) { this.tecnicoId = tecnicoId; }

    public String getTecnicoName() { return tecnicoName; }
    public void setTecnicoName(String tecnicoName) { this.tecnicoName = tecnicoName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }

    public LocalDateTime getOpenedAt() { return openedAt; }
    public void setOpenedAt(LocalDateTime openedAt) { this.openedAt = openedAt; }

    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }
}
