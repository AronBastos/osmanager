package com.osmanager.controller;

import com.osmanager.dto.ClientRequestDTO;
import com.osmanager.dto.ClientResponseDTO;
import com.osmanager.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ==============================================================
 * REST CONTROLLER — INTERVIEW NOTE (Topic 5)
 * ==============================================================
 * The Controller is a THIN layer. Its ONLY jobs:
 *   1. Receive an HTTP request (deserialise JSON → DTO)
 *   2. Validate the input (@Valid)
 *   3. Call the service
 *   4. Return the HTTP response (DTO → JSON + status code)
 *
 * NO business logic here — that belongs in the Service.
 * NO try/catch here — exceptions propagate to GlobalExceptionHandler.
 *
 * @RestController = @Controller + @ResponseBody
 *   → Every method's return value is serialised directly to JSON in the body.
 *   → No view resolution — this is a REST API, not an MVC web app.
 *
 * Q: "What is the difference between @Controller and @RestController?"
 * A: @Controller returns view names (for Thymeleaf/JSP rendering).
 *    @RestController = @Controller + @ResponseBody — every method's
 *    return value is serialised to JSON/XML directly in the response.
 *    In REST APIs, always use @RestController.
 *
 * @RequestMapping: sets the base URL path for ALL methods in this class.
 * ==============================================================
 *
 * ==============================================================
 * HTTP METHOD CONVENTIONS — INTERVIEW NOTE (Topic 5)
 * ==============================================================
 * GET    /api/clients        → list all (200 OK)
 * GET    /api/clients/{id}   → get one (200 OK or 404)
 * POST   /api/clients        → create  (201 Created + body)
 * PUT    /api/clients/{id}   → full update (200 OK)
 * PATCH  /api/clients/{id}   → partial update (200 OK)
 * DELETE /api/clients/{id}   → delete (204 No Content)
 *
 * Q: "What is the difference between PUT and PATCH?"
 * A: PUT replaces the ENTIRE resource (idempotent). PATCH applies
 *    a PARTIAL update. PUT with the same payload always gives the
 *    same result — PATCH may not.
 *
 * Q: "What is ResponseEntity and why use it?"
 * A: ResponseEntity wraps a body with an explicit HTTP status and headers.
 *    Returning an object directly always sends 200 OK. With ResponseEntity
 *    you control the code: 201 for creates, 204 for deletes, 404 for missing.
 * ==============================================================
 */
@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    // Constructor injection — no @Autowired needed (single constructor)
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * GET /api/clients
     * Returns all active clients.
     * 200 OK with an array body (even if empty — never return null for lists).
     */
    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> getAllClients() {
        List<ClientResponseDTO> clients = clientService.getAllActiveClients();
        return ResponseEntity.ok(clients);  // ok() = 200 OK
    }

    /**
     * GET /api/clients/{id}
     * Returns a single client or 404 if not found.
     *
     * @PathVariable extracts {id} from the URL.
     * ResourceNotFoundException → caught by GlobalExceptionHandler → 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getClientById(@PathVariable Long id) {
        ClientResponseDTO client = clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }

    /**
     * GET /api/clients/search?name=João
     *
     * @RequestParam extracts ?name=João from the query string.
     * required = false: the param is optional — returns all if missing.
     */
    @GetMapping("/search")
    public ResponseEntity<List<ClientResponseDTO>> searchClients(
            @RequestParam(required = false) String name) {
        List<ClientResponseDTO> clients = clientService.searchByName(name);
        return ResponseEntity.ok(clients);
    }

    /**
     * POST /api/clients
     * Creates a new client.
     *
     * @RequestBody deserialises the JSON body → ClientRequestDTO.
     * @Valid triggers Bean Validation. If any @NotBlank / @Email fails →
     *   MethodArgumentNotValidException → GlobalExceptionHandler → 400.
     *
     * Returns 201 Created (not 200!) with the newly created resource.
     * HTTP convention: POST that creates a resource → 201 Created.
     */
    @PostMapping
    public ResponseEntity<ClientResponseDTO> createClient(
            @Valid @RequestBody ClientRequestDTO dto) {
        ClientResponseDTO created = clientService.createClient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);  // 201 Created
    }

    /**
     * PUT /api/clients/{id}
     * Full update — replaces all fields.
     * Idempotent: calling PUT twice with the same body gives the same result.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequestDTO dto) {
        ClientResponseDTO updated = clientService.updateClient(id, dto);
        return ResponseEntity.ok(updated);  // 200 OK with the updated body
    }

    /**
     * DELETE /api/clients/{id}
     * Soft-delete (deactivates) the client.
     * Returns 204 No Content — DELETE never returns a body.
     *
     * HTTP convention: successful DELETE → 204 No Content (empty body).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateClient(@PathVariable Long id) {
        clientService.deactivateClient(id);
        return ResponseEntity.noContent().build();  // 204 No Content
    }

    /**
     * GET /api/clients/count
     * Dashboard endpoint — total active client count.
     * Returns a plain Long (wrapped in ResponseEntity).
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countActiveClients() {
        return ResponseEntity.ok(clientService.countActiveClients());
    }
}
