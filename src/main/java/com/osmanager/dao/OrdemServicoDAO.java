package com.osmanager.dao;

import com.osmanager.model.OrdemServico;
import com.osmanager.model.OrdemServico.StatusOrdemServico;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Data Access Object para a entidade OrdemServico
 */
public class OrdemServicoDAO extends GenericDAO<OrdemServico, Long> {

    public OrdemServicoDAO() {
        super(OrdemServico.class);
    }

    /**
     * Busca ordens de serviço por cliente
     */
    public List<OrdemServico> findByCliente(Long clienteId) {
        Query query = em.createQuery(
            "SELECT os FROM OrdemServico os WHERE os.cliente.id = :clienteId ORDER BY os.dataAbertura DESC");
        query.setParameter("clienteId", clienteId);
        return query.getResultList();
    }

    /**
     * Busca ordens de serviço por técnico
     */
    public List<OrdemServico> findByTecnico(Long tecnicoId) {
        Query query = em.createQuery(
            "SELECT os FROM OrdemServico os WHERE os.tecnico.id = :tecnicoId ORDER BY os.dataAbertura DESC");
        query.setParameter("tecnicoId", tecnicoId);
        return query.getResultList();
    }

    /**
     * Busca ordens de serviço por status
     */
    public List<OrdemServico> findByStatus(StatusOrdemServico status) {
        Query query = em.createQuery(
            "SELECT os FROM OrdemServico os WHERE os.status = :status ORDER BY os.dataAbertura DESC");
        query.setParameter("status", status);
        return query.getResultList();
    }

    /**
     * Busca ordens de serviço abertas
     */
    public List<OrdemServico> findOpenOrders() {
        return findByStatus(StatusOrdemServico.ABERTA);
    }

    /**
     * Busca ordens de serviço em andamento
     */
    public List<OrdemServico> findInProgressOrders() {
        return findByStatus(StatusOrdemServico.EM_ANDAMENTO);
    }

    /**
     * Conta ordens de serviço por status
     */
    public Long countByStatus(StatusOrdemServico status) {
        Query query = em.createQuery(
            "SELECT COUNT(os) FROM OrdemServico os WHERE os.status = :status");
        query.setParameter("status", status);
        return (Long) query.getSingleResult();
    }
}
