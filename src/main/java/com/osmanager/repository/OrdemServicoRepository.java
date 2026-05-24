package com.osmanager.repository;

import com.osmanager.model.OrdemServico;
import com.osmanager.model.OrdemServico.Status;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ==============================================================
 * N+1 PROBLEM & @EntityGraph — INTERVIEW NOTE (Topic 6)
 * ==============================================================
 * The N+1 problem is one of the most frequently asked JPA topics.
 *
 * THE PROBLEM:
 *   OrdemServico has @ManyToOne(fetch = LAZY) for Client and Tecnico.
 *   If you do:
 *     List<OrdemServico> orders = repo.findAll();          // 1 query
 *     for (OrdemServico o : orders) {
 *         String name = o.getClient().getName();           // N queries!
 *     }
 *   → For 10 orders: 1 + 10 = 11 queries. For 100: 101 queries!
 *
 * THE FIX — Option 1: @EntityGraph
 *   Tells JPA to load the specified associations via a single JOIN query.
 *   @EntityGraph(attributePaths = {"client", "tecnico"})
 *   → One query: SELECT os.*, c.*, t.* FROM ordens_servico os
 *                JOIN clients c ON os.client_id = c.id
 *                JOIN tecnicos t ON os.tecnico_id = t.id
 *
 * THE FIX — Option 2: JOIN FETCH in @Query
 *   @Query("SELECT os FROM OrdemServico os JOIN FETCH os.client JOIN FETCH os.tecnico")
 *   Equivalent to @EntityGraph but written explicitly in JPQL.
 *   Use @Query when you need additional WHERE/ORDER BY conditions.
 *
 * Q: "What is the N+1 problem? How do you solve it?"
 * A: Loading N entities triggers N additional queries for their
 *    associations. Fix with @EntityGraph or JOIN FETCH to load
 *    everything in one SQL query.
 * ==============================================================
 */
@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    // ==============================================================
    // @EntityGraph — solves N+1 without changing the method query
    // ==============================================================
    // This override adds a JOIN to the standard findById() query.
    // Spring Data JPA merges the EntityGraph with the derived query.
    // ==============================================================

    /**
     * Finds a single order AND eagerly loads client + tecnico in one query.
     * Use this when you know you'll need both associations.
     */
    @EntityGraph(attributePaths = {"client", "tecnico"})
    Optional<OrdemServico> findById(Long id);

    /**
     * Finds all orders — SOLVES N+1 via JOIN FETCH.
     * Without this, iterating and calling getClient() would fire N queries.
     */
    @Query("SELECT os FROM OrdemServico os JOIN FETCH os.client JOIN FETCH os.tecnico " +
           "ORDER BY os.openedAt DESC")
    List<OrdemServico> findAllWithClientAndTecnico();

    // Method-derived queries — Spring generates JPQL from the name

    /** All orders for a specific client — useful for client history view. */
    @EntityGraph(attributePaths = {"tecnico"})  // client is already known from the parameter
    List<OrdemServico> findByClientIdOrderByOpenedAtDesc(Long clientId);

    /** All orders assigned to a specific technician. */
    @EntityGraph(attributePaths = {"client"})
    List<OrdemServico> findByTecnicoIdOrderByOpenedAtDesc(Long tecnicoId);

    /** Orders by status — used in dashboard (open orders count, etc.). */
    @EntityGraph(attributePaths = {"client", "tecnico"})
    List<OrdemServico> findByStatusOrderByOpenedAtAsc(Status status);

    /** Orders opened in a date range — for monthly reports. */
    @Query("SELECT os FROM OrdemServico os JOIN FETCH os.client JOIN FETCH os.tecnico " +
           "WHERE os.openedAt BETWEEN :start AND :end ORDER BY os.openedAt DESC")
    List<OrdemServico> findByOpenedAtBetween(
            @Param("start") LocalDateTime start,
            @Param("end")   LocalDateTime end
    );

    /** Count orders by status — dashboard metric without loading entities. */
    Long countByStatus(Status status);

    /**
     * Revenue summary query — returns a scalar, not an entity.
     * Uses COALESCE to avoid NULL when no orders exist for the period.
     *
     * NOTE: This returns a Double (sum of BigDecimal) — in production,
     * consider using a Projection interface for type-safe scalar queries.
     */
    @Query("SELECT COALESCE(SUM(os.finalAmount), 0) FROM OrdemServico os " +
           "WHERE os.status = 'DONE' AND os.closedAt BETWEEN :start AND :end")
    Double sumRevenueByPeriod(@Param("start") LocalDateTime start,
                              @Param("end")   LocalDateTime end);
}
