package com.osmanager.repository;

import com.osmanager.model.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Technician repository — Spring Data JPA.
 * Inherits all standard CRUD operations from JpaRepository.
 */
@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {

    /** All active technicians, sorted by name. */
    List<Tecnico> findByActiveOrderByNameAsc(Boolean active);

    /** Find by specialty — useful for assigning the right technician. */
    List<Tecnico> findBySpecialtyContainingIgnoreCaseAndActive(String specialty, Boolean active);

    Optional<Tecnico> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    /**
     * Find technicians who currently have OPEN or IN_PROGRESS orders.
     * Uses a subquery to avoid loading unnecessary data.
     *
     * JPQL NOTE: "os.tecnico.id = t.id" navigates the relationship
     * by field, not by column name — JPA handles the JOIN for us.
     */
    @Query("SELECT DISTINCT t FROM Tecnico t WHERE t.active = true " +
           "AND EXISTS (SELECT os FROM OrdemServico os WHERE os.tecnico.id = t.id " +
           "            AND os.status IN ('OPEN', 'IN_PROGRESS'))")
    List<Tecnico> findActiveTecnicosWithOpenOrders();
}
