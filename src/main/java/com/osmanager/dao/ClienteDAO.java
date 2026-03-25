package com.osmanager.dao;

import com.osmanager.model.Cliente;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Data Access Object para a entidade Cliente
 */
public class ClienteDAO extends GenericDAO<Cliente, Long> {

    public ClienteDAO() {
        super(Cliente.class);
    }

    /**
     * Busca cliente por CPF
     */
    public Cliente findByCpf(String cpf) {
        try {
            Query query = em.createQuery("SELECT c FROM Cliente c WHERE c.cpf = :cpf");
            query.setParameter("cpf", cpf);
            return (Cliente) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Busca cliente por Email
     */
    public Cliente findByEmail(String email) {
        try {
            Query query = em.createQuery("SELECT c FROM Cliente c WHERE c.email = :email");
            query.setParameter("email", email);
            return (Cliente) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Busca todos os clientes ativos
     */
    public List<Cliente> findAllActive() {
        Query query = em.createQuery("SELECT c FROM Cliente c WHERE c.active = true ORDER BY c.name");
        return query.getResultList();
    }

    /**
     * Busca clientes por nome (LIKE)
     */
    public List<Cliente> findByName(String name) {
        Query query = em.createQuery("SELECT c FROM Cliente c WHERE c.name LIKE :name ORDER BY c.name");
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }

    /**
     * Desativa um cliente
     */
    public void deactivate(Long clientId) {
        Cliente client = findById(clientId);
        if (client != null) {
            client.setActive(false);
            update(client);
        }
    }
}
