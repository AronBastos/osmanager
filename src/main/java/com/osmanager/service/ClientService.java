package com.osmanager.service;

import com.osmanager.dto.ClientRequestDTO;
import com.osmanager.dto.ClientResponseDTO;
import com.osmanager.exception.BusinessException;
import com.osmanager.exception.ResourceNotFoundException;
import com.osmanager.model.Client;
import com.osmanager.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ==============================================================
 * SERVICE LAYER — INTERVIEW NOTE (Topics 2, 3, 7)
 * ==============================================================
 * The Service is the BUSINESS LOGIC layer. Its responsibilities:
 *   1. Orchestrate calls to one or more repositories
 *   2. Apply business rules and validations
 *   3. Manage transactions (@Transactional)
 *   4. Map entities ↔ DTOs
 *
 * Controllers should be THIN: receive request, call service, return response.
 * ALL business logic belongs HERE — never in controllers or repositories.
 * ==============================================================
 *
 * ==============================================================
 * @Service — INTERVIEW NOTE (Topic 3)
 * ==============================================================
 * @Service is a specialisation of @Component.
 * Functionally identical (registers a Spring bean) — but signals intent:
 * "This class contains business logic."
 *
 * Q: "What is the difference between @Component, @Service, @Repository?"
 * A: All three register a bean. @Repository ALSO adds exception translation
 *    (wraps DB exceptions into Spring's DataAccessException hierarchy).
 *    @Service is semantic — no extra behaviour. Use the most specific one.
 * ==============================================================
 *
 * ==============================================================
 * DEPENDENCY INJECTION — INTERVIEW NOTE (Topic 3)
 * ==============================================================
 * ALWAYS use CONSTRUCTOR INJECTION:
 *   + Dependencies are EXPLICIT (you can see what this class needs)
 *   + Fields can be FINAL — guaranteed immutable after construction
 *   + Simplifies unit testing — just pass mocks to the constructor
 *   + No reflection needed — @Autowired field injection requires it
 *
 * @Autowired on the constructor is OPTIONAL since Spring 4.3 when
 * there is only ONE constructor — Spring injects automatically.
 *
 * Q: "Why prefer constructor injection over field injection?"
 * A: Field injection hides dependencies, prevents final fields, and
 *    makes testing harder. Constructor injection is explicit, testable,
 *    and enables immutability.
 * ==============================================================
 */
@Service
public class ClientService {

    // final = this dependency is set once and never replaced (immutable)
    private final ClientRepository clientRepository;

    // Spring detects this single constructor and injects automatically.
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // ==============================================================
    // @Transactional — INTERVIEW NOTE (Topic 7)
    // ==============================================================
    // Wraps the method in a DB transaction. If a RuntimeException is
    // thrown, ALL DB changes in this method are ROLLED BACK atomically.
    //
    // Default settings:
    //   readOnly  = false     → can read AND write
    //   propagation = REQUIRED → join existing tx if present; create new if not
    //   rollbackFor = RuntimeException and subclasses
    //
    // BEST PRACTICE on read methods:
    //   @Transactional(readOnly = true) → disables Hibernate's dirty checking
    //   (comparing all managed entities to their DB snapshot on commit).
    //   For read-heavy apps, this can significantly improve performance.
    // ==============================================================

    /** Creates a new client. Write operation — default @Transactional. */
    @Transactional
    public ClientResponseDTO createClient(ClientRequestDTO dto) {
        // Business rule: CPF must be unique (if provided)
        if (dto.getCpf() != null && !dto.getCpf().isBlank()) {
            if (clientRepository.existsByCpf(dto.getCpf())) {
                // Unchecked exception → triggers transaction rollback automatically
                throw new BusinessException("CPF already registered: " + dto.getCpf());
            }
        }

        Client client = toEntity(dto);
        Client saved = clientRepository.save(client);  // save() triggers INSERT
        return ClientResponseDTO.from(saved);
    }

    /**
     * Returns all active clients.
     *
     * ==============================================================
     * STREAMS & LAMBDAS — INTERVIEW NOTE (Topic 2)
     * ==============================================================
     * Stream pipeline: source → intermediate ops → terminal op
     *
     * .stream()                    → opens the stream pipeline
     * .map(ClientResponseDTO::from) → METHOD REFERENCE (shorthand lambda)
     *                                 same as: client -> ClientResponseDTO.from(client)
     *                                 transforms each Client → ClientResponseDTO
     * .collect(Collectors.toList()) → TERMINAL op: materialises into List
     *
     * KEY POINTS:
     *   - Streams are LAZY — ops run only when a terminal op is called
     *   - The original List<Client> is NEVER modified
     *   - collect() is the most common terminal op in Spring services
     * ==============================================================
     */
    @Transactional(readOnly = true)
    public List<ClientResponseDTO> getAllActiveClients() {
        return clientRepository.findByActiveOrderByNameAsc(true)
                .stream()
                .map(ClientResponseDTO::from)       // method reference
                .collect(Collectors.toList());
    }

    /**
     * Finds a client by ID.
     *
     * ==============================================================
     * OPTIONAL — INTERVIEW NOTE (Topic 2)
     * ==============================================================
     * Optional<T> is a container that may or may not hold a value.
     * findById() returns Optional<Client> — we must handle the empty case.
     *
     * .orElseThrow(supplier) → unwrap or throw if empty (PREFERRED pattern)
     * .orElse(default)       → get value or return a default
     * .map(fn)               → transform if present, skip if empty
     * .ifPresent(consumer)   → run action only if a value is present
     *
     * NEVER use .get() without .isPresent() — it throws NoSuchElementException.
     *
     * Q: "What is Optional and why use it?"
     * A: Makes the absence of a value EXPLICIT in the return type.
     *    Replaces null returns and forces callers to handle the missing case.
     * ==============================================================
     */
    @Transactional(readOnly = true)
    public ClientResponseDTO getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        return ClientResponseDTO.from(client);
    }

    /**
     * Searches clients by partial name — combined Lambda + Stream + Optional example.
     *
     * ==============================================================
     * LAMBDA EXPRESSION — INTERVIEW NOTE (Topic 2)
     * ==============================================================
     * Pre-Java 8 (anonymous class):
     *   clients.stream().filter(new Predicate<Client>() {
     *       public boolean test(Client c) { return c.getActive(); }
     *   }).collect(...)
     *
     * Java 8 lambda:
     *   clients.stream().filter(c -> c.getActive()).collect(...)
     *
     * Method reference (even cleaner — delegates to an existing method):
     *   clients.stream().filter(Client::getActive).collect(...)
     * ==============================================================
     */
    @Transactional(readOnly = true)
    public List<ClientResponseDTO> searchByName(String name) {
        if (name == null || name.isBlank()) {
            return getAllActiveClients();
        }
        return clientRepository.searchByName(name.trim())
                .stream()
                .map(ClientResponseDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing client.
     * Demonstrates dirty checking — no explicit save() for managed entities.
     *
     * ==============================================================
     * HIBERNATE DIRTY CHECKING — INTERVIEW NOTE (Topic 6)
     * ==============================================================
     * When you load an entity inside a @Transactional method, Hibernate
     * stores a snapshot of its original state. On transaction commit,
     * it compares the current state to the snapshot. If anything changed,
     * it automatically fires an UPDATE statement — no save() needed.
     *
     * You CAN call save() explicitly (it's harmless) — but understanding
     * dirty checking is crucial for debugging unexpected UPDATEs.
     * ==============================================================
     */
    @Transactional
    public ClientResponseDTO updateClient(Long id, ClientRequestDTO dto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (clientRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
                throw new BusinessException("Email already in use: " + dto.getEmail());
            }
        }

        applyDtoToEntity(dto, client);
        // No save() call — dirty checking will emit the UPDATE on commit
        return ClientResponseDTO.from(client);
    }

    /**
     * Soft-deletes a client — preserves history.
     *
     * SOFT DELETE PATTERN: set active = false instead of DELETE.
     * Avoids cascading FK errors and preserves service order history.
     */
    @Transactional
    public void deactivateClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        if (Boolean.FALSE.equals(client.getActive())) {
            throw new BusinessException("Client is already inactive");
        }
        client.setActive(false);
        // dirty checking fires the UPDATE on commit — no explicit save()
    }

    /** Hard-deletes a client. */
    @Transactional
    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client not found with id: " + id);
        }
        clientRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Long countActiveClients() {
        return clientRepository.countActiveClients();
    }

    // ---- Private mapping helpers ----

    private Client toEntity(ClientRequestDTO dto) {
        Client client = new Client();
        applyDtoToEntity(dto, client);
        return client;
    }

    private void applyDtoToEntity(ClientRequestDTO dto, Client client) {
        client.setName(dto.getName());
        client.setEmail(dto.getEmail());
        client.setPhone(dto.getPhone());
        client.setCpf(dto.getCpf());
        client.setAddress(dto.getAddress());
        client.setCity(dto.getCity());
        client.setState(dto.getState());
        client.setZipCode(dto.getZipCode());
    }
}
