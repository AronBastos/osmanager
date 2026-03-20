package aron.bastos.osmanager.service;

import aron.bastos.osmanager.dao.ClientDAO;
import aron.bastos.osmanager.model.Client;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

/**
 * Serviço de negócio para Cliente
 * Contém a lógica de negócio e orquestração
 */
@Stateless
public class ClientService {

    @Inject
    private ClientDAO clientDAO;

    /**
     * Cria um novo cliente com validações
     */
    public Client createClient(Client client) throws Exception {
        // Validações
        if (client == null || client.getName() == null || client.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório");
        }

        if (client.getCpf() != null && !client.getCpf().isEmpty()) {
            Client existingClient = clientDAO.findByCpf(client.getCpf());
            if (existingClient != null) {
                throw new IllegalArgumentException("CPF já cadastrado: " + client.getCpf());
            }
        }

        if (client.getEmail() != null && !client.getEmail().isEmpty()) {
            if (!isValidEmail(client.getEmail())) {
                throw new IllegalArgumentException("Email inválido: " + client.getEmail());
            }
        }

        client.setActive(true);
        clientDAO.create(client);
        return client;
    }

    /**
     * Atualiza um cliente existente
     */
    public Client updateClient(Client client) throws Exception {
        if (client.getId() == null) {
            throw new IllegalArgumentException("ID do cliente é obrigatório para atualização");
        }

        Client existing = clientDAO.findById(client.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Cliente não encontrado: " + client.getId());
        }

        return clientDAO.update(client);
    }

    /**
     * Busca cliente por ID
     */
    public Client getClientById(Long id) {
        return clientDAO.findById(id);
    }

    /**
     * Lista todos os clientes ativos
     */
    public List<Client> getAllActiveClients() {
        return clientDAO.findAllActive();
    }

    /**
     * Busca clientes por nome
     */
    public List<Client> searchClientsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return clientDAO.findAllActive();
        }
        return clientDAO.findByName(name);
    }

    /**
     * Desativa um cliente
     */
    public void deactivateClient(Long clientId) throws Exception {
        Client client = clientDAO.findById(clientId);
        if (client == null) {
            throw new IllegalArgumentException("Cliente não encontrado: " + clientId);
        }
        clientDAO.deactivate(clientId);
    }

    /**
     * Deleta um cliente
     */
    public void deleteClient(Long clientId) throws Exception {
        Client client = clientDAO.findById(clientId);
        if (client == null) {
            throw new IllegalArgumentException("Cliente não encontrado: " + clientId);
        }
        clientDAO.deleteById(clientId);
    }

    /**
     * Conta total de clientes
     */
    public Long countClients() {
        return clientDAO.count();
    }

    /**
     * Valida email
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}

