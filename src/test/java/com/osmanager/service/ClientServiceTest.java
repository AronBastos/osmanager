package com.osmanager.service;

import com.osmanager.dto.ClientRequestDTO;
import com.osmanager.dto.ClientResponseDTO;
import com.osmanager.exception.BusinessException;
import com.osmanager.exception.ResourceNotFoundException;
import com.osmanager.model.Client;
import com.osmanager.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ==============================================================
 * UNIT TEST WITH MOCKITO — INTERVIEW NOTE (Topic 9)
 * ==============================================================
 * WHAT is a unit test?
 *   Tests a SINGLE class in complete ISOLATION from its dependencies.
 *   Dependencies are replaced by MOCKS — objects that simulate real
 *   behaviour without hitting DB, network, or filesystem.
 *
 * WHY unit tests?
 *   Fast (milliseconds), deterministic, run without any infrastructure.
 *   Provide a safety net for refactoring — they fail immediately if
 *   behaviour changes.
 *
 * ANNOTATION BREAKDOWN:
 *
 * @ExtendWith(MockitoExtension.class)
 *   → Activates Mockito in JUnit 5 — processes @Mock and @InjectMocks.
 *   → Alternative to calling MockitoAnnotations.openMocks(this) manually.
 *   → NO Spring context is loaded — this is a pure unit test.
 *
 * @Mock
 *   → Creates a mock object for ClientRepository.
 *   → All methods return default values (null for objects, 0 for numbers)
 *     UNLESS stubbed with when(...).thenReturn(...).
 *   → The real ClientRepository never runs — no DB calls.
 *
 * @InjectMocks
 *   → Creates a REAL instance of ClientService.
 *   → Injects the @Mock ClientRepository into it (via constructor injection).
 *   → This is why CONSTRUCTOR injection is easier to test: Mockito just
 *     calls new ClientService(mockedRepository) under the hood.
 *
 * Q: "What is the difference between @Mock and @Spy?"
 * A: @Mock creates a completely fake object — all methods are stubbed.
 *    @Spy wraps a REAL object — only the methods you stub are intercepted;
 *    the rest delegate to the real implementation.
 * ==============================================================
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClientService Unit Tests")
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    // Test fixtures — set up once per test method
    private Client testClient;
    private ClientRequestDTO testDTO;

    @BeforeEach
    void setUp() {
        // @BeforeEach: runs before EACH @Test method — isolates test state
        testClient = new Client("João Silva", "joao@email.com", "(11) 99999-0000", "123.456.789-00");
        testClient.setId(1L);
        testClient.setActive(true);

        testDTO = new ClientRequestDTO("João Silva", "joao@email.com", "(11) 99999-0000", "123.456.789-00");
    }

    // ==============================================================
    // TEST NAMING CONVENTION — should_ExpectedBehaviour_When_Condition
    // or given_When_Then (BDD style)
    // ==============================================================

    @Test
    @DisplayName("Should create client successfully when CPF is unique")
    void shouldCreateClient_WhenCpfIsUnique() {
        // ==============================================================
        // ARRANGE — INTERVIEW NOTE: AAA Pattern (Arrange, Act, Assert)
        // ==============================================================
        // Stub: when existsByCpf is called with any String → return false
        // This simulates "CPF not yet registered" — no DB lookup needed.
        when(clientRepository.existsByCpf(anyString())).thenReturn(false);
        // Stub: when save() is called with any Client → return testClient
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        // ACT — call the method under test
        ClientResponseDTO result = clientService.createClient(testDTO);

        // ASSERT — verify the result
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("João Silva");
        assertThat(result.getEmail()).isEqualTo("joao@email.com");

        // VERIFY — ensure the collaboration happened as expected
        // times(1): save() was called exactly once
        verify(clientRepository, times(1)).save(any(Client.class));
        verify(clientRepository, times(1)).existsByCpf("123.456.789-00");
    }

    @Test
    @DisplayName("Should throw BusinessException when CPF is already registered")
    void shouldThrowBusinessException_WhenCpfIsDuplicate() {
        // Stub: CPF already exists in DB
        when(clientRepository.existsByCpf(anyString())).thenReturn(true);

        // ==============================================================
        // ASSERTJ EXCEPTION ASSERTION — INTERVIEW NOTE (Topic 9)
        // ==============================================================
        // assertThatThrownBy: executes a lambda and asserts an exception is thrown.
        // Preferred over @Test(expected = ...) — can also assert the message.
        //
        // Alternative syntax:
        //   assertThrows(BusinessException.class, () -> clientService.createClient(dto));
        // (from JUnit 5 — also valid, but AssertJ's version checks the message too)
        // ==============================================================
        assertThatThrownBy(() -> clientService.createClient(testDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("CPF already registered");

        // Verify save() was NEVER called — if CPF is dup, we abort early
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Should return client by id")
    void shouldReturnClient_WhenIdExists() {
        // ==============================================================
        // MOCKING Optional — INTERVIEW NOTE
        // ==============================================================
        // findById() returns Optional<Client>. Mock it with Optional.of(entity).
        // Optional.empty() simulates the not-found case.
        // ==============================================================
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));

        ClientResponseDTO result = clientService.getClientById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("João Silva");
        verify(clientRepository).findById(1L);  // times(1) is the default
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when client id does not exist")
    void shouldThrowResourceNotFoundException_WhenIdNotFound() {
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.getClientById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("Should return all active clients as DTOs")
    void shouldReturnAllActiveClients() {
        // Stub: repository returns a list with one client
        Client client2 = new Client("Maria Santos", "maria@email.com", null, null);
        client2.setId(2L);
        client2.setActive(true);

        when(clientRepository.findByActiveOrderByNameAsc(true))
                .thenReturn(List.of(testClient, client2));

        List<ClientResponseDTO> result = clientService.getAllActiveClients();

        // AssertJ fluent assertions on collections
        assertThat(result)
                .hasSize(2)
                .extracting(ClientResponseDTO::getName)   // extract names from DTOs
                .containsExactly("João Silva", "Maria Santos");
    }

    @Test
    @DisplayName("Should deactivate client successfully")
    void shouldDeactivateClient_WhenActive() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));

        clientService.deactivateClient(1L);

        // Verify the client's active flag was changed
        // Mockito captures the actual object — we can assert its state
        assertThat(testClient.getActive()).isFalse();
        // No explicit save() is called — dirty checking handles the UPDATE
        // But we can verify it was NOT explicitly saved (asserting the pattern)
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when deactivating already inactive client")
    void shouldThrowBusinessException_WhenClientAlreadyInactive() {
        testClient.setActive(false);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));

        assertThatThrownBy(() -> clientService.deactivateClient(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already inactive");
    }

    @Test
    @DisplayName("Should delete client when exists")
    void shouldDeleteClient_WhenExists() {
        when(clientRepository.existsById(1L)).thenReturn(true);
        doNothing().when(clientRepository).deleteById(1L);

        // Should not throw
        assertThatCode(() -> clientService.deleteClient(1L))
                .doesNotThrowAnyException();

        verify(clientRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should return empty list when no active clients")
    void shouldReturnEmptyList_WhenNoActiveClients() {
        when(clientRepository.findByActiveOrderByNameAsc(true))
                .thenReturn(List.of());

        List<ClientResponseDTO> result = clientService.getAllActiveClients();

        assertThat(result).isEmpty();
        // Convention: never return null for list endpoints — always empty list
    }
}
