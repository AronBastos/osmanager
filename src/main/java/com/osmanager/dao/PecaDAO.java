package com.osmanager.dao;

import com.osmanager.model.Peca;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Data Access Object para a entidade Peca
 */
public class PecaDAO extends GenericDAO<Peca, Long> {

    public PecaDAO() {
        super(Peca.class);
    }

    /**
     * Busca peça por código
     */
    public Peca findByCodigo(String codigo) {
        try {
            Query query = em.createQuery("SELECT p FROM Peca p WHERE p.codigo = :codigo");
            query.setParameter("codigo", codigo);
            return (Peca) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Busca peças por nome (LIKE)
     */
    public List<Peca> findByNome(String nome) {
        Query query = em.createQuery(
            "SELECT p FROM Peca p WHERE p.nome LIKE :nome AND p.ativo = true ORDER BY p.nome");
        query.setParameter("nome", "%" + nome + "%");
        return query.getResultList();
    }

    /**
     * Busca peças por categoria
     */
    public List<Peca> findByCategoria(String categoria) {
        Query query = em.createQuery(
            "SELECT p FROM Peca p WHERE p.categoria = :categoria AND p.ativo = true ORDER BY p.nome");
        query.setParameter("categoria", categoria);
        return query.getResultList();
    }

    /**
     * Lista todas as peças ativas
     */
    public List<Peca> findAllActive() {
        Query query = em.createQuery(
            "SELECT p FROM Peca p WHERE p.ativo = true ORDER BY p.nome");
        return query.getResultList();
    }

    /**
     * Busca peças com estoque baixo
     */
    public List<Peca> findLowStock() {
        Query query = em.createQuery(
            "SELECT p FROM Peca p WHERE p.quantidadeEstoque <= p.quantidadeMinima AND p.ativo = true ORDER BY p.quantidadeEstoque");
        return query.getResultList();
    }

    /**
     * Busca peças por marca
     */
    public List<Peca> findByMarca(String marca) {
        Query query = em.createQuery(
            "SELECT p FROM Peca p WHERE p.marca = :marca AND p.ativo = true ORDER BY p.nome");
        query.setParameter("marca", marca);
        return query.getResultList();
    }

    /**
     * Busca peças com preço entre valores
     */
    public List<Peca> findByPrecoRange(double minPreco, double maxPreco) {
        Query query = em.createQuery(
            "SELECT p FROM Peca p WHERE p.precoVenda BETWEEN :minPreco AND :maxPreco AND p.ativo = true ORDER BY p.precoVenda");
        query.setParameter("minPreco", minPreco);
        query.setParameter("maxPreco", maxPreco);
        return query.getResultList();
    }

    /**
     * Desativa uma peça
     */
    public void deactivate(Long pecaId) {
        Peca peca = findById(pecaId);
        if (peca != null) {
            peca.setAtivo(false);
            update(peca);
        }
    }

    /**
     * Atualiza quantidade em estoque
     */
    public void updateStock(Long pecaId, int quantidade) {
        Peca peca = findById(pecaId);
        if (peca != null) {
            peca.setQuantidadeEstoque(quantidade);
            update(peca);
        }
    }
}
