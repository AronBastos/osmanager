package com.osmanager.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Utilitário para gerenciamento de EntityManager JPA
 * Fornece métodos para obter EntityManager e gerenciar transações
 */
public class JpaUtil {

    private static final String PERSISTENCE_UNIT_NAME = "osmanagerPU";
    private static EntityManagerFactory factory;

    static {
        try {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar EntityManagerFactory: " + e.getMessage(), e);
        }
    }

    /**
     * Obtém uma instância de EntityManager
     * @return EntityManager
     */
    public static EntityManager getEntityManager() {
        return factory.createEntityManager();
    }

    /**
     * Fecha o EntityManagerFactory
     */
    public static void closeFactory() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }

    /**
     * Verifica se o EntityManagerFactory está aberto
     * @return true se aberto
     */
    public static boolean isFactoryOpen() {
        return factory != null && factory.isOpen();
    }
}
