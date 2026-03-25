package com.osmanager.service;

import com.osmanager.dao.ClienteDAO;
import com.osmanager.model.Cliente;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

/**
 * Serviço de negócio para Cliente
 * Contém a lógica de negócio e orquestração
 */
@Stateless
public class ClienteService {

    private static final Logger logger = LogManager.getLogger(ClienteService.class);

    @Inject
    private ClienteDAO clienteDAO;

    /**
     * Cria um novo cliente com validações
     */
    public Cliente createClient(Cliente client) throws Exception {
        logger.info("Criando novo cliente: {}", client.getName());
        // Validações
        if (client == null || client.getName() == null || client.getName().trim().isEmpty()) {
            logger.warn("Tentativa de criar cliente com nome inválido");
            throw new IllegalArgumentException("Nome do cliente é obrigatório");
        }

        if (client.getCpf() != null && !client.getCpf().isEmpty()) {
            Cliente existingClient = clienteDAO.findByCpf(client.getCpf());
            if (existingClient != null) {
                logger.warn("CPF já cadastrado: {}", client.getCpf());
                throw new IllegalArgumentException("CPF já cadastrado: " + client.getCpf());
            }
        }

        if (client.getEmail() != null && !client.getEmail().isEmpty()) {
            if (!isValidEmail(client.getEmail())) {
                logger.warn("Email inválido: {}", client.getEmail());
                throw new IllegalArgumentException("Email inválido: " + client.getEmail());
            }
        }

        client.setActive(true);
        clienteDAO.create(client);
        logger.info("Cliente criado com sucesso: ID {}", client.getId());
        return client;
    }

    /**
     * Atualiza um cliente existente
     */
    public Cliente updateClient(Cliente client) throws Exception {
        logger.info("Atualizando cliente ID: {}", client.getId());
        if (client.getId() == null) {
            logger.warn("Tentativa de atualizar cliente sem ID");
            throw new IllegalArgumentException("ID do cliente é obrigatório para atualização");
        }

        Cliente existing = clienteDAO.findById(client.getId());
        if (existing == null) {
            logger.warn("Cliente não encontrado para atualização: {}", client.getId());
            throw new IllegalArgumentException("Cliente não encontrado: " + client.getId());
        }

        Cliente updated = clienteDAO.update(client);
        logger.info("Cliente atualizado com sucesso: ID {}", client.getId());
        return updated;
    }

    /**
     * Busca cliente por ID
     */
    public Cliente getClientById(Long id) {
        logger.debug("Buscando cliente por ID: {}", id);
        return clienteDAO.findById(id);
    }

    /**
     * Lista todos os clientes ativos
     */
    public List<Cliente> getAllActiveClients() {
        logger.debug("Listando todos os clientes ativos");
        return clienteDAO.findAllActive();
    }

    /**
     * Busca clientes por nome
     */
    public List<Cliente> searchClientsByName(String name) {
        logger.debug("Buscando clientes por nome: {}", name);
        if (name == null || name.trim().isEmpty()) {
            return clienteDAO.findAllActive();
        }
        return clienteDAO.findByName(name);
    }

    /**
     * Desativa um cliente
     */
    public void deactivateClient(Long clientId) throws Exception {
        logger.info("Desativando cliente ID: {}", clientId);
        Cliente client = clienteDAO.findById(clientId);
        if (client == null) {
            logger.warn("Cliente não encontrado para desativação: {}", clientId);
            throw new IllegalArgumentException("Cliente não encontrado: " + clientId);
        }
        clienteDAO.deactivate(clientId);
        logger.info("Cliente desativado com sucesso: ID {}", clientId);
    }

    /**
     * Deleta um cliente
     */
    public void deleteClient(Long clientId) throws Exception {
        logger.info("Deletando cliente ID: {}", clientId);
        Cliente client = clienteDAO.findById(clientId);
        if (client == null) {
            logger.warn("Cliente não encontrado para deleção: {}", clientId);
            throw new IllegalArgumentException("Cliente não encontrado: " + clientId);
        }
        clienteDAO.deleteById(clientId);
        logger.info("Cliente deletado com sucesso: ID {}", clientId);
    }

    /**
     * Conta total de clientes
     */
    public Long countClients() {
        logger.debug("Contando total de clientes");
        return clienteDAO.count();
    }

    /**
     * Valida email
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
