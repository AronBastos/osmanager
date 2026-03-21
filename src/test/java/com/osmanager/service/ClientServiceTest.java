package com.osmanager.service;

import com.osmanager.dao.ClientDAO;
import com.osmanager.model.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ClientService
 */
class ClientServiceTest {

    @Mock
    private ClientDAO clientDAO;

    @InjectMocks
    private ClientService clientService;

    private Client testClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testClient = new Client();
        testClient.setId(1L);
        testClient.setName("João Silva");
        testClient.setEmail("joao@email.com");
        testClient.setPhone("(11) 98765-4321");
        testClient.setCpf("12345678901");
    }

    @Test
    void testCreateClientSuccess() throws Exception {
        // Arrange
        when(clientDAO.findByCpf(testClient.getCpf())).thenReturn(null);

        // Act
        clientService.createClient(testClient);

        // Assert
        verify(clientDAO, times(1)).create(testClient);
        assertTrue(testClient.getActive());
    }

    @Test
    void testCreateClientWithNullName() {
        // Arrange
        testClient.setName(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            clientService.createClient(testClient);
        });
    }

    @Test
    void testCreateClientWithDuplicateCpf() {
        // Arrange
        Client existingClient = new Client();
        existingClient.setId(2L);
        when(clientDAO.findByCpf(testClient.getCpf())).thenReturn(existingClient);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            clientService.createClient(testClient);
        });
    }

    @Test
    void testGetClientById() {
        // Arrange
        when(clientDAO.findById(1L)).thenReturn(testClient);

        // Act
        Client result = clientService.getClientById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testClient.getId(), result.getId());
        assertEquals(testClient.getName(), result.getName());
        verify(clientDAO, times(1)).findById(1L);
    }

    @Test
    void testUpdateClient() throws Exception {
        // Arrange
        Client existingClient = new Client();
        existingClient.setId(1L);
        existingClient.setName("Nome Antigo");

        when(clientDAO.findById(1L)).thenReturn(existingClient);
        when(clientDAO.update(testClient)).thenReturn(testClient);

        // Act
        Client result = clientService.updateClient(testClient);

        // Assert
        assertNotNull(result);
        verify(clientDAO, times(1)).update(testClient);
    }

    @Test
    void testUpdateClientWithNullId() {
        // Arrange
        testClient.setId(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            clientService.updateClient(testClient);
        });
    }

    @Test
    void testDeleteClient() throws Exception {
        // Arrange
        when(clientDAO.findById(1L)).thenReturn(testClient);

        // Act
        clientService.deleteClient(1L);

        // Assert
        verify(clientDAO, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteClientNotFound() {
        // Arrange
        when(clientDAO.findById(999L)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            clientService.deleteClient(999L);
        });
    }

    @Test
    void testValidateEmail() throws Exception {
        // Arrange
        Client validEmailClient = new Client();
        validEmailClient.setName("Test");
        validEmailClient.setEmail("test@example.com");

        when(clientDAO.findByCpf(null)).thenReturn(null);

        // Act & Assert
        assertDoesNotThrow(() -> clientService.createClient(validEmailClient));
    }

    @Test
    void testInvalidEmail() {
        // Arrange
        testClient.setEmail("invalid-email");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            clientService.createClient(testClient);
        });
    }
}
