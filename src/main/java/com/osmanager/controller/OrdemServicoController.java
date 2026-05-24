package com.osmanager.controller;

import com.osmanager.dto.OrdemServicoRequestDTO;
import com.osmanager.dto.OrdemServicoResponseDTO;
import com.osmanager.model.OrdemServico.Status;
import com.osmanager.service.OrdemServicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Service Order REST Controller.
 *
 * Base URL: /api/orders
 *
 * Endpoints:
 *   GET    /api/orders                  → list all
 *   GET    /api/orders/{id}             → get one
 *   GET    /api/orders?status=OPEN      → filter by status
 *   GET    /api/orders/client/{clientId}→ orders for a client
 *   POST   /api/orders                  → create
 *   PATCH  /api/orders/{id}/start       → start (OPEN → IN_PROGRESS)
 *   PATCH  /api/orders/{id}/complete    → complete (IN_PROGRESS → DONE)
 *   PATCH  /api/orders/{id}/cancel      → cancel
 *
 * NOTE: Status transitions use PATCH (partial update of the status field),
 * not PUT (which would be a full replacement of all fields).
 * This is the correct REST convention for state machine transitions.
 */
@RestController
@RequestMapping("/api/orders")
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;

    public OrdemServicoController(OrdemServicoService ordemServicoService) {
        this.ordemServicoService = ordemServicoService;
    }

    /**
     * GET /api/orders or GET /api/orders?status=OPEN
     *
     * @RequestParam(required = false): status filter is optional.
     * When present, delegates to getOrdersByStatus().
     * When absent, returns all orders.
     *
     * Single endpoint handles both cases — avoids duplicate routes.
     */
    @GetMapping
    public ResponseEntity<List<OrdemServicoResponseDTO>> getOrders(
            @RequestParam(required = false) Status status) {
        List<OrdemServicoResponseDTO> orders = status != null
                ? ordemServicoService.getOrdersByStatus(status)
                : ordemServicoService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /** GET /api/orders/{id} — single order with client and technician. */
    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoService.getOrderById(id));
    }

    /** GET /api/orders/client/{clientId} — all orders for a specific client. */
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<OrdemServicoResponseDTO>> getOrdersByClient(
            @PathVariable Long clientId) {
        return ResponseEntity.ok(ordemServicoService.getOrdersByClient(clientId));
    }

    /**
     * POST /api/orders — create new order.
     * Returns 201 Created with the created order in the response body.
     */
    @PostMapping
    public ResponseEntity<OrdemServicoResponseDTO> createOrder(
            @Valid @RequestBody OrdemServicoRequestDTO dto) {
        OrdemServicoResponseDTO created = ordemServicoService.createOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PATCH /api/orders/{id}/start — transitions OPEN → IN_PROGRESS.
     *
     * Uses PATCH because only the STATUS field changes.
     * Not PUT — that would imply replacing all fields.
     * No request body needed — the transition is implied by the URL action.
     */
    @PatchMapping("/{id}/start")
    public ResponseEntity<OrdemServicoResponseDTO> startOrder(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoService.startOrder(id));
    }

    /** PATCH /api/orders/{id}/complete — transitions IN_PROGRESS → DONE. */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<OrdemServicoResponseDTO> completeOrder(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoService.completeOrder(id));
    }

    /** PATCH /api/orders/{id}/cancel — cancels the order. */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrdemServicoResponseDTO> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoService.cancelOrder(id));
    }

    /** GET /api/orders/count?status=OPEN — dashboard metrics. */
    @GetMapping("/count")
    public ResponseEntity<Long> countByStatus(@RequestParam Status status) {
        return ResponseEntity.ok(ordemServicoService.countByStatus(status));
    }
}
