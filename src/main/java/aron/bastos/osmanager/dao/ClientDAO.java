package aron.bastos.osmanager.dao;

import aron.bastos.osmanager.model.Client;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Data Access Object para a entidade Client
 */
public class ClientDAO extends GenericDAO<Client, Long> {

    public ClientDAO() {
        super(Client.class);
    }

    /**
     * Busca cliente por CPF
     */
    public Client findByCpf(String cpf) {
        try {
            Query query = em.createQuery("SELECT c FROM Client c WHERE c.cpf = :cpf");
            query.setParameter("cpf", cpf);
            return (Client) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Busca cliente por Email
     */
    public Client findByEmail(String email) {
        try {
            Query query = em.createQuery("SELECT c FROM Client c WHERE c.email = :email");
            query.setParameter("email", email);
            return (Client) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Busca todos os clientes ativos
     */
    public List<Client> findAllActive() {
        Query query = em.createQuery("SELECT c FROM Client c WHERE c.active = true ORDER BY c.name");
        return query.getResultList();
    }

    /**
     * Busca clientes por nome (LIKE)
     */
    public List<Client> findByName(String name) {
        Query query = em.createQuery("SELECT c FROM Client c WHERE c.name LIKE :name ORDER BY c.name");
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }

    /**
     * Desativa um cliente
     */
    public void deactivate(Long clientId) {
        Client client = findById(clientId);
        if (client != null) {
            client.setActive(false);
            update(client);
        }
    }
}

