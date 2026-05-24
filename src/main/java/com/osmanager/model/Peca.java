package com.osmanager.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Part (Peça) entity — a spare part stocked by the workshop.
 * Demonstrates additional JPA annotations and business logic encapsulation.
 */
@Entity
@Table(
    name = "pecas",
    indexes = {
        @Index(name = "idx_peca_nome",     columnList = "nome"),
        @Index(name = "idx_peca_codigo",   columnList = "codigo"),
        @Index(name = "idx_peca_categoria",columnList = "categoria")
    }
)
public class Peca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // unique = true → DB-level uniqueness constraint (in addition to business validation)
    @Column(name = "codigo", length = 50, unique = true)
    private String code;

    @Column(name = "nome", length = 150, nullable = false)
    private String name;

    @Column(name = "descricao", length = 500)
    private String description;

    @Column(name = "categoria", length = 100)
    private String category;

    @Column(name = "marca", length = 100)
    private String brand;

    // precision = total digits, scale = decimal digits
    // BigDecimal is mandatory for money — NEVER use double/float for currency
    @Column(name = "preco_custo", precision = 10, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "preco_venda", precision = 10, scale = 2, nullable = false)
    private BigDecimal salePrice;

    @Column(name = "quantidade_estoque", nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "quantidade_minima")
    private Integer minimumQuantity = 0;

    @Column(name = "ativo")
    private Boolean active = true;

    // ==============================================================
    // ENCAPSULATION — business logic inside the entity (Topic 1)
    // ==============================================================
    // This is a DOMAIN METHOD: isLowStock() expresses business knowledge.
    // Callers don't need to know the comparison logic — they just ask.
    // This is the difference between an ANEMIC model (pure data bag)
    // and a RICH model (data + behaviour together).
    // ==============================================================

    /** Returns true if current stock is at or below the minimum threshold. */
    public boolean isLowStock() {
        return stockQuantity != null && minimumQuantity != null
                && stockQuantity <= minimumQuantity;
    }

    /** Deduct quantity when a part is used in a service order. */
    public void deductStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to deduct must be positive");
        }
        if (this.stockQuantity < quantity) {
            throw new IllegalStateException("Insufficient stock for part: " + name);
        }
        this.stockQuantity -= quantity;
    }

    // ---- Constructors ----

    public Peca() {}

    public Peca(String code, String name, BigDecimal salePrice, Integer stockQuantity) {
        this.code = code;
        this.name = name;
        this.salePrice = salePrice;
        this.stockQuantity = stockQuantity;
    }

    // ---- Getters and Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }

    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Integer getMinimumQuantity() { return minimumQuantity; }
    public void setMinimumQuantity(Integer minimumQuantity) { this.minimumQuantity = minimumQuantity; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "Peca{id=" + id + ", code='" + code + "', name='" + name
                + "', stock=" + stockQuantity + "}";
    }
}
