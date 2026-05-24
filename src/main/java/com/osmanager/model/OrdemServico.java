package com.osmanager.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ==============================================================
 * SERVICE ORDER ENTITY — JPA RELATIONSHIPS (Topic 6)
 * ==============================================================
 * This entity demonstrates the most important JPA concepts for
 * interviews: relationships, fetch strategies, N+1, lifecycle
 * callbacks, and enum mapping.
 *
 * Database table: "ordens_servico"
 * Relationships:
 *   → @ManyToOne Client  (many orders belong to one client)
 *   → @ManyToOne Tecnico (many orders assigned to one technician)
 * ==============================================================
 */
@Entity
@Table(
    name = "ordens_servico",
    indexes = {
        @Index(name = "idx_os_client",        columnList = "client_id"),
        @Index(name = "idx_os_tecnico",       columnList = "tecnico_id"),
        @Index(name = "idx_os_status",        columnList = "status"),
        @Index(name = "idx_os_data_abertura", columnList = "data_abertura")
    }
)
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==============================================================
    // @ManyToOne — INTERVIEW NOTE (Topic 6)
    // ==============================================================
    // Many orders → One client.
    //
    // fetch = FetchType.LAZY
    //   → The Client is NOT loaded from DB until you call getClient().
    //   → DEFAULT for @ManyToOne is EAGER — always override to LAZY
    //     unless you know you always need the client data.
    //
    // N+1 PROBLEM — the most common interview question about JPA:
    //   Scenario: load 10 orders, then call getClient().getName() in a loop.
    //   Result: 1 query for orders + 10 queries for each client = 11 total.
    //   Fix: Use @EntityGraph or JOIN FETCH in the repository query.
    //   See: OrdemServicoRepository.findAllWithClientAndTecnico()
    // ==============================================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id", nullable = false)
    private Tecnico tecnico;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "data_fechamento")
    private LocalDateTime closedAt;

    // ==============================================================
    // @Enumerated — INTERVIEW NOTE (Topic 6)
    // ==============================================================
    // EnumType.STRING stores the name as varchar → "OPEN", "IN_PROGRESS"
    //   SAFE to add/reorder enum values without breaking existing data.
    //
    // EnumType.ORDINAL (AVOID!) stores the int position (0, 1, 2...).
    //   Reordering the enum silently corrupts all existing DB rows.
    //   Always use STRING in production.
    // ==============================================================
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status = Status.OPEN;

    @Column(name = "descricao", length = 1000, nullable = false)
    private String description;

    @Column(name = "observacoes", length = 1000)
    private String notes;

    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "desconto", precision = 10, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    // Derived field — always recalculated before persist/update
    @Column(name = "valor_final", precision = 10, scale = 2)
    private BigDecimal finalAmount = BigDecimal.ZERO;

    // ==============================================================
    // JPA Lifecycle Callbacks
    // ==============================================================
    // @PrePersist: runs BEFORE first INSERT — auto-sets openedAt.
    // @PreUpdate: runs BEFORE each UPDATE — recalculates finalAmount.
    // These run INSIDE the JPA transaction, before the SQL is sent.
    // ==============================================================

    @PrePersist
    protected void onCreate() {
        if (openedAt == null) {
            openedAt = LocalDateTime.now();
        }
        recalculateFinalAmount();
    }

    @PreUpdate
    protected void onUpdate() {
        recalculateFinalAmount();
    }

    /**
     * Business rule: finalAmount = totalAmount - discount.
     * Private — enforces ENCAPSULATION: only this class manages its calculation.
     */
    private void recalculateFinalAmount() {
        if (totalAmount != null && discount != null) {
            finalAmount = totalAmount.subtract(discount);
        }
    }

    // ==============================================================
    // Enum for Order Status — static nested type
    // ==============================================================
    // Logically belongs to OrdemServico — avoids polluting the package.
    // ==============================================================
    public enum Status {
        OPEN,         // Just created, awaiting start
        IN_PROGRESS,  // Technician is working
        DONE,         // Work complete, awaiting payment
        CANCELLED     // Cancelled — no work performed
    }

    // ---- Constructors ----

    public OrdemServico() {}

    public OrdemServico(Client client, Tecnico tecnico, String description) {
        this.client = client;
        this.tecnico = tecnico;
        this.description = description;
    }

    // ---- Getters and Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Tecnico getTecnico() { return tecnico; }
    public void setTecnico(Tecnico tecnico) { this.tecnico = tecnico; }

    public LocalDateTime getOpenedAt() { return openedAt; }
    public void setOpenedAt(LocalDateTime openedAt) { this.openedAt = openedAt; }

    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    // finalAmount is read-only — always derived via @PrePersist/@PreUpdate
    public BigDecimal getFinalAmount() { return finalAmount; }

    @Override
    public String toString() {
        return "OrdemServico{id=" + id + ", status=" + status
                + ", client=" + (client != null ? client.getName() : null) + "}";
    }
}
