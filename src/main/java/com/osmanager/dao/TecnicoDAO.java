package com.osmanager.dao;

import com.osmanager.model.Tecnico;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Data Access Object para a entidade Tecnico
 */
public class TecnicoDAO extends GenericDAO<Tecnico, Long> {

    public TecnicoDAO() {
        super(Tecnico.class);
    }

    /**
     * Busca técnico por CPF
     */
    public Tecnico findByCpf(String cpf) {
        try {
            Query query = em.createQuery("SELECT t FROM Tecnico t WHERE t.cpf = :cpf");
            query.setParameter("cpf", cpf);
            return (Tecnico) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Busca técnico por Email
     */
    public Tecnico findByEmail(String email) {
        try {
            Query query = em.createQuery("SELECT t FROM Tecnico t WHERE t.email = :email");
            query.setParameter("email", email);
            return (Tecnico) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Busca técnicos por nome (LIKE)
     */
    public List<Tecnico> findByNome(String nome) {
        Query query = em.createQuery("SELECT t FROM Tecnico t WHERE t.name LIKE :nome ORDER BY t.name");
        query.setParameter("nome", "%" + nome + "%");
        return query.getResultList();
    }

    /**
     * Busca técnicos por especialidade
     */
    public List<Tecnico> findByEspecialidade(String especialidade) {
        Query query = em.createQuery("SELECT t FROM Tecnico t WHERE t.specialty = :especialidade AND t.active = true ORDER BY t.name");
        query.setParameter("especialidade", especialidade);
        return query.getResultList();
    }

    /**
     * Lista todos os técnicos ativos
     */
    public List<Tecnico> findAllActive() {
        Query query = em.createQuery("SELECT t FROM Tecnico t WHERE t.active = true ORDER BY t.name");
        return query.getResultList();
    }

    /**
     * Desativa um técnico
     */
    public void deactivate(Long tecnicoId) {
        Tecnico tecnico = findById(tecnicoId);
        if (tecnico != null) {
            tecnico.setActive(false);
            update(tecnico);
        }
    }
}
