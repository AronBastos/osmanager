package com.osmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osmanager.dto.ClientRequestDTO;
import com.osmanager.dto.ClientResponseDTO;
import com.osmanager.exception.GlobalExceptionHandler;
import com.osmanager.exception.ResourceNotFoundException;
import com.osmanager.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ==============================================================
 * CONTROLLER SLICE TEST — @WebMvcTest (Topic 9)
 * ==============================================================
 * WHAT is @WebMvcTest?
 *   A SLICE test that loads ONLY the web layer (controllers, filters,
 *   advice, converters). Does NOT load @Service, @Repository, or the DB.
 *   It tests HTTP request/response mapping — NOT business logic.
 *
 * WHY use it instead of @SpringBootTest?
 *   Fast startup (loads a fraction of the context).
 *   Isolates the controller from the service (service is mocked).
 *   Tests: URL routing, HTTP method, status codes, JSON shape, @Valid.
 *
 * @WebMvcTest(ClientController.class)
 *   → Loads only ClientController (and related beans: exception handler).
 *   → ClientService is NOT a Spring bean here — it's mocked.
 *
 * @MockBean
 *   → Creates a Mockito mock AND registers it as a Spring bean.
 *   → Different from @Mock: @MockBean integrates with the Spring context,
 *     so Spring can inject it into ClientController.
 *   → Use @MockBean in @WebMvcTest / @SpringBootTest contexts.
 *     Use @Mock in plain @ExtendWith(MockitoExtension.class) unit tests.
 *
 * MockMvc
 *   → Simulates HTTP requests without starting a real server.
 *   → Faster than TestRestTemplate (which starts a full server).
 *
 * Q: "When do you use @WebMvcTest vs @SpringBootTest?"
 * A: @WebMvcTest for testing the controller layer in isolation — fast,
 *    no DB. @SpringBootTest for end-to-end integration tests — loads
 *    everything, can hit a real DB (or use @Transactional + H2).
 * ==============================================================
 */
@WebMvcTest(ClientController.class)
@DisplayName("ClientController @WebMvcTest Tests")
class ClientControllerTest {

    // MockMvc is auto-configured by @WebMvcTest — no setup needed
    @Autowired
    private MockMvc mockMvc;

    // Jackson ObjectMapper — auto-configured — for serialising test DTOs to JSON
    @Autowired
    private ObjectMapper objectMapper;

    // @MockBean: Spring registers this mock bean — ClientController can inject it
    @MockBean
    private ClientService clientService;

    private ClientResponseDTO sampleResponse;
    private ClientRequestDTO sampleRequest;

    @BeforeEach
    void setUp() {
        sampleResponse = new ClientResponseDTO();
        sampleResponse.setId(1L);
        sampleResponse.setName("João Silva");
        sampleResponse.setEmail("joao@email.com");
        sampleResponse.setPhone("(11) 99999-0000");
        sampleResponse.setCpf("123.456.789-00");
        sampleResponse.setActive(true);
        sampleResponse.setRole("CLIENT");

        sampleRequest = new ClientRequestDTO("João Silva", "joao@email.com", "(11) 99999-0000", "123.456.789-00");
    }

    // ==============================================================
    // GET /api/clients — list all
    // ==============================================================

    @Test
    @DisplayName("GET /api/clients — should return 200 with client list")
    void getAllClients_Returns200WithList() throws Exception {
        // Stub: service returns a list with one client
        when(clientService.getAllActiveClients()).thenReturn(List.of(sampleResponse));

        // MockMvc: perform GET /api/clients
        mockMvc.perform(get("/api/clients")
                        .accept(MediaType.APPLICATION_JSON))
                // ASSERT HTTP status
                .andExpect(status().isOk())                              // 200
                // ASSERT JSON body — JsonPath expressions navigate the JSON tree
                .andExpect(jsonPath("$").isArray())                      // root is array
                .andExpect(jsonPath("$.length()").value(1))             // 1 element
                .andExpect(jsonPath("$[0].name").value("João Silva"))   // first item.name
                .andExpect(jsonPath("$[0].id").value(1));               // first item.id
    }

    @Test
    @DisplayName("GET /api/clients — should return empty array when no clients")
    void getAllClients_Returns200WithEmptyList() throws Exception {
        when(clientService.getAllActiveClients()).thenReturn(List.of());

        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
        // Note: 200 OK with [] is CORRECT — never return 204 for empty lists
    }

    // ==============================================================
    // GET /api/clients/{id}
    // ==============================================================

    @Test
    @DisplayName("GET /api/clients/{id} — should return 200 when client exists")
    void getClientById_Returns200_WhenFound() throws Exception {
        when(clientService.getClientById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    @DisplayName("GET /api/clients/{id} — should return 404 when client not found")
    void getClientById_Returns404_WhenNotFound() throws Exception {
        // The service throws ResourceNotFoundException → GlobalExceptionHandler → 404
        when(clientService.getClientById(999L))
                .thenThrow(new ResourceNotFoundException("Client not found with id: 999"));

        mockMvc.perform(get("/api/clients/999"))
                .andExpect(status().isNotFound())                        // 404
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Client not found with id: 999"));
    }

    // ==============================================================
    // POST /api/clients — create
    // ==============================================================

    @Test
    @DisplayName("POST /api/clients — should return 201 when data is valid")
    void createClient_Returns201_WhenValid() throws Exception {
        when(clientService.createClient(any(ClientRequestDTO.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        // objectMapper serialises the DTO to JSON string
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())                         // 201 Created
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("João Silva"));
    }

    @Test
    @DisplayName("POST /api/clients — should return 400 when name is blank")
    void createClient_Returns400_WhenNameIsBlank() throws Exception {
        // Create a DTO with invalid data — name is blank
        ClientRequestDTO invalidDTO = new ClientRequestDTO("", "joao@email.com", null, null);

        // @Valid triggers validation → MethodArgumentNotValidException → 400
        // The GlobalExceptionHandler returns structured error body
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())                      // 400
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.name").exists());    // field-level error
    }

    @Test
    @DisplayName("POST /api/clients — should return 400 when email is invalid")
    void createClient_Returns400_WhenEmailInvalid() throws Exception {
        ClientRequestDTO invalidDTO = new ClientRequestDTO("João", "not-an-email", null, null);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    // ==============================================================
    // DELETE /api/clients/{id}
    // ==============================================================

    @Test
    @DisplayName("DELETE /api/clients/{id} — should return 204 No Content")
    void deactivateClient_Returns204() throws Exception {
        doNothing().when(clientService).deactivateClient(1L);

        mockMvc.perform(delete("/api/clients/1"))
                .andExpect(status().isNoContent());                      // 204 No Content
        // No body expected — 204 responses have no body by definition
    }

    @Test
    @DisplayName("DELETE /api/clients/{id} — should return 404 when not found")
    void deactivateClient_Returns404_WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Client not found with id: 42"))
                .when(clientService).deactivateClient(42L);

        mockMvc.perform(delete("/api/clients/42"))
                .andExpect(status().isNotFound());
    }
}
