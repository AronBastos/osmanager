package com.osmanager.service;

import com.osmanager.dto.OrdemServicoRequestDTO;
import com.osmanager.dto.OrdemServicoResponseDTO;
import com.osmanager.exception.BusinessException;
import com.osmanager.exception.ResourceNotFoundException;
import com.osmanager.model.Client;
import com.osmanager.model.OrdemServico;
import com.osmanager.model.OrdemServico.Status;
import com.osmanager.model.Tecnico;
import com.osmanager.repository.ClientRepository;
import com.osmanager.repository.OrdemServicoRepository;
import com.osmanager.repository.TecnicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Order business service.
 *
 * ==============================================================
 * @Transactional PROPAGATION — INTERVIEW NOTE (Topic 7)
 * ==============================================================
 * Propagation controls what happens when a @Transactional method
 * is called FROM WITHIN another transaction.
 *
 * REQUIRED (default):
 *   Join the existing tx. If no tx exists, create a new one.
 *   Most common — use for all standard service operations.
 *
 * REQUIRES_NEW:
 *   Always creates a NEW transaction. Suspends any existing one.
 *   Use case: AUDIT LOGGING — must persist even if the main tx
 *   rolls back. The audit record survives even on failure.
 *
 * SUPPORTS:
 *   Join if exists; run WITHOUT a tx if not.
 *   Use for read operations that can run in or out of a tx.
 *
 * NOT_SUPPORTED:
 *   Always run without a transaction. Suspends the existing one.
 *
 * NEVER:
 *   MUST NOT be in a transaction — throws exception if one exists.
 *
 * Q: "What is @Transactional and when should you use it?"
 * A: It wraps the method in a DB transaction. If a RuntimeException
 *    is thrown, all changes are rolled back atomically. Use on
 *    service methods that perform multiple writes that must succeed
 *    or fail together. Add readOnly=true on read-only methods.
 * ==============================================================
 */
@Service
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClientRepository clientRepository;
    private final TecnicoRepository tecnicoRepository;

    // Constructor injection — all three dependencies required
    public OrdemServicoService(
            OrdemServicoRepository ordemServicoRepository,
            ClientRepository clientRepository,
            TecnicoRepository tecnicoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.clientRepository = clientRepository;
        this.tecnicoRepository = tecnicoRepository;
    }

    /**
     * Creates a new service order.
     * Propagation.REQUIRED (default): joins any enclosing transaction.
     */
    @Transactional
    public OrdemServicoResponseDTO createOrder(OrdemServicoRequestDTO dto) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client not found with id: " + dto.getClientId()));

        if (Boolean.FALSE.equals(client.getActive())) {
            throw new BusinessException("Cannot create order for inactive client");
        }

        Tecnico tecnico = tecnicoRepository.findById(dto.getTecnicoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Technician not found with id: " + dto.getTecnicoId()));

        if (Boolean.FALSE.equals(tecnico.getActive())) {
            throw new BusinessException("Cannot assign order to inactive technician");
        }

        OrdemServico order = new OrdemServico(client, tecnico, dto.getDescription());
        order.setNotes(dto.getNotes());
        order.setTotalAmount(dto.getTotalAmount());
        order.setDiscount(dto.getDiscount());
        // @PrePersist will set openedAt and recalculate finalAmount before INSERT

        OrdemServico saved = ordemServicoRepository.save(order);
        return OrdemServicoResponseDTO.from(saved);
    }

    /**
     * Returns all orders — uses JOIN FETCH (N+1 prevention).
     *
     * ==============================================================
     * STREAMS WITH COMPLEX MAPPING — INTERVIEW NOTE (Topic 2)
     * ==============================================================
     * The map() call here uses a method reference that receives an
     * already-loaded entity (client and tecnico are JOIN FETCHed).
     * Safe to call getClient().getName() — no lazy loading triggered.
     *
     * If we used findAll() instead of findAllWithClientAndTecnico(),
     * each OrdemServicoResponseDTO.from(os) call would trigger
     * TWO extra queries (os.getClient() and os.getTecnico()) — N+1!
     * ==============================================================
     */
    @Transactional(readOnly = true)
    public List<OrdemServicoResponseDTO> getAllOrders() {
        return ordemServicoRepository.findAllWithClientAndTecnico()
                .stream()
                .map(OrdemServicoResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrdemServicoResponseDTO getOrderById(Long id) {
        OrdemServico order = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return OrdemServicoResponseDTO.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrdemServicoResponseDTO> getOrdersByClient(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client not found with id: " + clientId);
        }
        return ordemServicoRepository.findByClientIdOrderByOpenedAtDesc(clientId)
                .stream()
                .map(OrdemServicoResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrdemServicoResponseDTO> getOrdersByStatus(Status status) {
        return ordemServicoRepository.findByStatusOrderByOpenedAtAsc(status)
                .stream()
                .map(OrdemServicoResponseDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * Marks an order as IN_PROGRESS.
     * Business rule: only OPEN orders can be started.
     *
     * Shows STATE MACHINE pattern — status transitions have rules.
     */
    @Transactional
    public OrdemServicoResponseDTO startOrder(Long id) {
        OrdemServico order = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (order.getStatus() != Status.OPEN) {
            throw new BusinessException(
                    "Only OPEN orders can be started. Current status: " + order.getStatus());
        }

        order.setStatus(Status.IN_PROGRESS);
        // dirty checking fires UPDATE on commit — no explicit save() needed
        return OrdemServicoResponseDTO.from(order);
    }

    /**
     * Completes an order.
     *
     * ==============================================================
     * PROPAGATION.REQUIRED — Default behaviour
     * ==============================================================
     * If an outer @Transactional method calls this, they share ONE tx.
     * If called standalone, a new tx is created.
     * Both the order update AND any side effects happen atomically.
     * ==============================================================
     */
    @Transactional
    public OrdemServicoResponseDTO completeOrder(Long id) {
        OrdemServico order = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (order.getStatus() == Status.DONE) {
            throw new BusinessException("Order is already completed");
        }
        if (order.getStatus() == Status.CANCELLED) {
            throw new BusinessException("Cannot complete a cancelled order");
        }

        order.setStatus(Status.DONE);
        order.setClosedAt(LocalDateTime.now());
        // @PreUpdate will recalculate finalAmount before UPDATE

        return OrdemServicoResponseDTO.from(order);
    }

    /**
     * Cancels an order.
     *
     * ==============================================================
     * PROPAGATION.REQUIRES_NEW — INTERVIEW EXAMPLE (Topic 7)
     * ==============================================================
     * If we had an audit log service called here, it would use
     * REQUIRES_NEW so the audit record is saved independently:
     *
     *   @Transactional(propagation = Propagation.REQUIRES_NEW)
     *   public void logCancellation(Long orderId, String reason) {
     *       // This runs in its own transaction.
     *       // Even if the main cancelOrder() tx rolls back,
     *       // the audit log record will be committed.
     *   }
     * ==============================================================
     */
    @Transactional
    public OrdemServicoResponseDTO cancelOrder(Long id) {
        OrdemServico order = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (order.getStatus() == Status.DONE) {
            throw new BusinessException("Cannot cancel a completed order");
        }
        if (order.getStatus() == Status.CANCELLED) {
            throw new BusinessException("Order is already cancelled");
        }

        order.setStatus(Status.CANCELLED);
        order.setClosedAt(LocalDateTime.now());

        return OrdemServicoResponseDTO.from(order);
    }

    /**
     * Dashboard metric — count by status.
     * readOnly = true: no writes, disable dirty checking for performance.
     */
    @Transactional(readOnly = true)
    public Long countByStatus(Status status) {
        return ordemServicoRepository.countByStatus(status);
    }

    /**
     * Revenue for a period.
     *
     * Stream example using method reference chain + Optional.orElse:
     *   Double revenue = repo.sumRevenueByPeriod(start, end);
     *   return Optional.ofNullable(revenue).orElse(0.0);
     *
     * (The JPQL already uses COALESCE so null is unlikely, but defensive coding)
     */
    @Transactional(readOnly = true)
    public Double getRevenue(LocalDateTime start, LocalDateTime end) {
        Double revenue = ordemServicoRepository.sumRevenueByPeriod(start, end);
        return revenue != null ? revenue : 0.0;
    }
}
