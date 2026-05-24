package com.osmanager.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ==============================================================
 * OOP PILLAR 2 — INHERITANCE (Topic 1)
 * ==============================================================
 * Client EXTENDS Person — inheriting id, name, email, phone, cpf,
 * active, and createdAt fields. No duplication needed.
 *
 * Q: "What is the risk of overusing inheritance?"
 * A: Tight coupling between parent and child. Changes to Person affect
 *    ALL subclasses. For unrelated behaviours (e.g., notifications),
 *    prefer COMPOSITION: Client HAS-A Notifier, not IS-A Notifier.
 *    Rule: "Favour composition over inheritance."
 *
 * JPA NOTE: Because Person is @MappedSuperclass (not @Entity),
 * Client gets its own table "clients" with ALL fields from both
 * Person and Client merged into one flat table — no JOIN needed.
 * ==============================================================
 *
 * ==============================================================
 * JPA ENTITY — INTERVIEW NOTE (Topic 6)
 * ==============================================================
 * @Entity: tells JPA to map this class to a database table.
 * @Table: customises the table name and declares indexes for performance.
 *
 * Q: "What is JPA and how is it different from Hibernate?"
 * A: JPA (Jakarta Persistence API) is the SPECIFICATION — a set of
 *    interfaces (@Entity, @Id, EntityManager). Hibernate is the most
 *    popular IMPLEMENTATION. Spring Data JPA adds another layer:
 *    JpaRepository with built-in CRUD + query derivation from method
 *    names. You write the interface; Spring generates the impl at runtime.
 * ==============================================================
 */
@Entity
@Table(
    name = "clients",
    indexes = {
        @Index(name = "idx_client_name", columnList = "nome"),
        @Index(name = "idx_client_email", columnList = "email"),
        // Index on CPF for fast lookup — CPF is unique per client
        @Index(name = "idx_client_cpf", columnList = "cpf")
    }
)
public class Client extends Person {

    // Client-specific fields (not inherited from Person)
    @Column(name = "endereco", length = 255)
    private String address;

    @Column(name = "cidade", length = 100)
    private String city;

    @Column(name = "estado", length = 2)
    private String state;

    @Column(name = "cep", length = 10)
    private String zipCode;

    // ==============================================================
    // JPA RELATIONSHIP — INTERVIEW NOTE (Topic 6)
    // ==============================================================
    // One Client can have MANY service orders.
    //
    // fetch = FetchType.LAZY (ALWAYS use LAZY for @OneToMany!)
    //   → Orders are NOT loaded until you call getOrders().
    //   → Prevents accidental loading of thousands of records.
    //
    // mappedBy = "client"
    //   → The FOREIGN KEY lives on the OrdemServico table (client_id column).
    //   → This is the "inverse" (non-owning) side of the relationship.
    //
    // [!] INTERVIEW TRAP: @OneToMany(fetch = FetchType.EAGER) is
    //     almost always WRONG. It causes N+1 queries and can load
    //     enormous data unexpectedly. Default for @OneToMany is LAZY — keep it.
    // ==============================================================
    @OneToMany(
        mappedBy = "client",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<OrdemServico> orders = new ArrayList<>();

    // ==============================================================
    // JPA LIFECYCLE CALLBACK
    // ==============================================================
    // @PrePersist runs BEFORE the entity is first INSERTed into DB.
    // @PreUpdate  runs BEFORE each UPDATE.
    // These are JPA hooks — in Spring beans you use @PostConstruct instead.
    // ==============================================================
    @PrePersist
    protected void prePersist() {
        if (getCreatedAt() == null) {
            setCreatedAt(java.time.LocalDateTime.now());
        }
    }

    // ==============================================================
    // OOP PILLAR 4 — POLYMORPHISM: concrete implementation
    // ==============================================================
    @Override
    public String getRole() {
        return "CLIENT";
        // At runtime: Person p = new Client(); p.getRole() → "CLIENT"
        // The JVM dispatches to THIS method, not Person's abstract one.
        // This is dynamic dispatch — the heart of runtime polymorphism.
    }

    // ---- Constructors ----

    public Client() {
        // JPA requires a no-arg constructor (can be protected or public)
    }

    public Client(String name, String email, String phone, String cpf) {
        setName(name);
        setEmail(email);
        setPhone(phone);
        setCpf(cpf);
    }

    // ---- Client-specific Getters and Setters ----

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public List<OrdemServico> getOrders() { return orders; }
    public void setOrders(List<OrdemServico> orders) { this.orders = orders; }

    @Override
    public String toString() {
        return "Client{id=" + getId() + ", name='" + getName() + "', cpf='" + getCpf() + "'}";
    }
}
