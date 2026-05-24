package com.osmanager.repository;

import com.osmanager.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ==============================================================
 * SPRING DATA JPA REPOSITORY — INTERVIEW NOTE (Topic 6)
 * ==============================================================
 * JpaRepository<Client, Long> provides out-of-the-box:
 *   save(entity)          → INSERT or UPDATE
 *   findById(id)          → SELECT by PK, returns Optional<Client>
 *   findAll()             → SELECT * FROM clients
 *   deleteById(id)        → DELETE by PK
 *   count()               → SELECT COUNT(*)
 *   existsById(id)        → boolean existence check
 *
 * Q: "How does Spring Data JPA generate implementations?"
 * A: At startup, Spring Data JPA scans for interfaces extending
 *    Repository. It creates a proxy implementation at runtime using
 *    reflection — you write zero SQL for standard CRUD operations.
 *
 * @Repository: marks this as a data-layer bean. Also enables
 * Spring's exception translation: raw JPA/JDBC exceptions are
 * wrapped into Spring's DataAccessException hierarchy, which is
 * unchecked — no more boilerplate try/catch in services.
 * ==============================================================
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    // ==============================================================
    // METHOD-DERIVED QUERIES — INTERVIEW NOTE (Topic 6)
    // ==============================================================
    // Spring Data parses the method name and generates the JPQL query.
    // Convention: findBy + FieldName + Condition
    //
    // findByActive(true) → "SELECT c FROM Client c WHERE c.active = ?1"
    //
    // These method names are PARSED AT STARTUP — if you typo a field
    // name, the app fails on boot, not at runtime. This is a safety net.
    // ==============================================================

    /** Find all active/inactive clients, ordered by name. */
    List<Client> findByActiveOrderByNameAsc(Boolean active);

    /** Find by CPF — Optional forces callers to handle the not-found case. */
    Optional<Client> findByCpf(String cpf);

    /** Find by email — case-sensitive exact match. */
    Optional<Client> findByEmail(String email);

    // ==============================================================
    // @Query — JPQL (Java Persistence Query Language)
    // ==============================================================
    // Use @Query when method-name derivation gets too verbose or when
    // you need JOIN, GROUP BY, subqueries, or complex WHERE clauses.
    //
    // JPQL vs SQL:
    //   JPQL operates on ENTITIES and FIELDS (not tables and columns).
    //   "c.name" refers to Client.name field, not the "nome" column.
    //   This keeps queries portable across databases (MySQL, PostgreSQL, etc.)
    // ==============================================================

    /**
     * Search clients by partial name — case-insensitive LIKE query.
     * LOWER() on both sides ensures case-insensitive matching.
     */
    @Query("SELECT c FROM Client c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND c.active = true ORDER BY c.name")
    List<Client> searchByName(@Param("name") String name);

    /**
     * Count active clients — useful for dashboard statistics.
     * Returns a scalar value, not an entity list.
     */
    @Query("SELECT COUNT(c) FROM Client c WHERE c.active = true")
    Long countActiveClients();

    /**
     * Check existence by CPF — avoids loading the full entity just to check.
     * More efficient than findByCpf().isPresent() for validation.
     */
    boolean existsByCpf(String cpf);

    /** Check if email is already taken by another client (exclude current id). */
    @Query("SELECT COUNT(c) > 0 FROM Client c WHERE c.email = :email AND c.id != :excludeId")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("excludeId") Long excludeId);
}
