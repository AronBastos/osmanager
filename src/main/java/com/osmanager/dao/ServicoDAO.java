package com.osmanager.dao;

import com.osmanager.model.Servico;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Data Access Object para a entidade Servico
 */
public class ServicoDAO extends GenericDAO<Servico, Long> {

    public ServicoDAO() {
        super(Servico.class);
    }

    /**
     * Busca serviços por nome (LIKE)
     */
    public List<Servico> findByNome(String nome) {
        Query query = em.createQuery(
            "SELECT s FROM Servico s WHERE s.nome LIKE :nome AND s.ativo = true ORDER BY s.nome");
        query.setParameter("nome", "%" + nome + "%");
        return query.getResultList();
    }

    /**
     * Busca serviços por categoria
     */
    public List<Servico> findByCategoria(String categoria) {
        Query query = em.createQuery(
            "SELECT s FROM Servico s WHERE s.categoria = :categoria AND s.ativo = true ORDER BY s.nome");
        query.setParameter("categoria", categoria);
        return query.getResultList();
    }

    /**
     * Lista todos os serviços ativos
     */
    public List<Servico> findAllActive() {
        Query query = em.createQuery(
            "SELECT s FROM Servico s WHERE s.ativo = true ORDER BY s.nome");
        return query.getResultList();
    }

    /**
     * Busca serviços com preço entre valores
     */
    public List<Servico> findByPrecoRange(double minPreco, double maxPreco) {
        Query query = em.createQuery(
            "SELECT s FROM Servico s WHERE s.preco BETWEEN :minPreco AND :maxPreco AND s.ativo = true ORDER BY s.preco");
        query.setParameter("minPreco", minPreco);
        query.setParameter("maxPreco", maxPreco);
        return query.getResultList();
    }

    /**
     * Desativa um serviço
     */
    public void deactivate(Long servicoId) {
        Servico servico = findById(servicoId);
        if (servico != null) {
            servico.setAtivo(false);
            update(servico);
        }
    }
}
