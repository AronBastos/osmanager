package com.osmanager.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * DAO Genérico com operações CRUD comuns
 * Todos os DAOs devem estender esta classe
 */
public abstract class GenericDAO<T, ID> {

    @PersistenceContext
    protected EntityManager em;

    private final Class<T> entityClass;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Persiste uma nova entidade
     */
    public T create(T entity) {
        em.persist(entity);
        return entity;
    }

    /**
     * Busca entidade por ID
     */
    public T findById(ID id) {
        return em.find(entityClass, id);
    }

    /**
     * Retorna todas as entidades
     */
    public List<T> findAll() {
        String query = String.format("SELECT e FROM %s e", entityClass.getSimpleName());
        return em.createQuery(query, entityClass).getResultList();
    }

    /**
     * Atualiza uma entidade
     */
    public T update(T entity) {
        return em.merge(entity);
    }

    /**
     * Deleta uma entidade por ID
     */
    public void deleteById(ID id) {
        T entity = findById(id);
        if (entity != null) {
            em.remove(entity);
        }
    }

    /**
     * Deleta uma entidade
     */
    public void delete(T entity) {
        em.remove(em.merge(entity));
    }

    /**
     * Retorna a quantidade de entidades
     */
    public Long count() {
        String query = String.format("SELECT COUNT(e) FROM %s e", entityClass.getSimpleName());
        return (Long) em.createQuery(query).getSingleResult();
    }

    /**
     * Verifica se uma entidade existe
     */
    public boolean exists(ID id) {
        return findById(id) != null;
    }
}
