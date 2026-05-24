package com.osmanager.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Service (Serviço) entity — a type of labour service offered by the workshop.
 * Examples: "Oil change", "Brake inspection", "Engine tune-up".
 */
@Entity
@Table(
    name = "servicos",
    indexes = {
        @Index(name = "idx_servico_nome",     columnList = "nome"),
        @Index(name = "idx_servico_categoria",columnList = "categoria")
    }
)
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", length = 150, nullable = false)
    private String name;

    @Column(name = "descricao", length = 500)
    private String description;

    @Column(name = "categoria", length = 100)
    private String category;

    // Standard price for this service — actual price may vary per order
    @Column(name = "preco", precision = 10, scale = 2, nullable = false)
    private BigDecimal basePrice;

    // Estimated hours to complete — used for scheduling
    @Column(name = "tempo_estimado_horas")
    private Integer estimatedHours;

    @Column(name = "ativo")
    private Boolean active = true;

    // ---- Constructors ----

    public Servico() {}

    public Servico(String name, String description, BigDecimal basePrice) {
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
    }

    // ---- Getters and Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public Integer getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(Integer estimatedHours) { this.estimatedHours = estimatedHours; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "Servico{id=" + id + ", name='" + name + "', basePrice=" + basePrice + "}";
    }
}
