package com.osmanager.service;

import com.osmanager.dao.TecnicoDAO;
import com.osmanager.model.Tecnico;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

/**
 * Serviço de negócio para Técnico
 */
@Stateless
public class TecnicoService {

    private static final Logger logger = LogManager.getLogger(TecnicoService.class);

    @Inject
    private TecnicoDAO tecnicoDAO;

    /**
     * Cria um novo técnico
     */
    public Tecnico createTecnico(Tecnico tecnico) throws Exception {
        logger.info("Criando novo técnico: {}", tecnico.getName());

        validateTecnico(tecnico);

        // Verifica CPF único
        if (tecnico.getCpf() != null && !tecnico.getCpf().isEmpty()) {
            Tecnico existing = tecnicoDAO.findByCpf(tecnico.getCpf());
            if (existing != null) {
                throw new IllegalArgumentException("CPF já cadastrado: " + tecnico.getCpf());
            }
        }

        tecnico.setActive(true);
        Tecnico created = tecnicoDAO.create(tecnico);
        logger.info("Técnico criado com sucesso: ID {}", created.getId());
        return created;
    }

    /**
     * Atualiza um técnico
     */
    public Tecnico updateTecnico(Tecnico tecnico) throws Exception {
        logger.info("Atualizando técnico ID: {}", tecnico.getId());

        if (tecnico.getId() == null) {
            throw new IllegalArgumentException("ID do técnico é obrigatório");
        }

        Tecnico existing = tecnicoDAO.findById(tecnico.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Técnico não encontrado: " + tecnico.getId());
        }

        validateTecnico(tecnico);

        Tecnico updated = tecnicoDAO.update(tecnico);
        logger.info("Técnico atualizado com sucesso: ID {}", tecnico.getId());
        return updated;
    }

    /**
     * Busca técnico por ID
     */
    public Tecnico getTecnicoById(Long id) {
        logger.debug("Buscando técnico por ID: {}", id);
        return tecnicoDAO.findById(id);
    }

    /**
     * Lista todos os técnicos ativos
     */
    public List<Tecnico> getAllActiveTecnicos() {
        logger.debug("Listando todos os técnicos ativos");
        return tecnicoDAO.findAllActive();
    }

    /**
     * Busca técnicos por nome
     */
    public List<Tecnico> searchTecnicosByNome(String nome) {
        logger.debug("Buscando técnicos por nome: {}", nome);
        if (nome == null || nome.trim().isEmpty()) {
            return tecnicoDAO.findAllActive();
        }
        return tecnicoDAO.findByNome(nome);
    }

    /**
     * Busca técnicos por especialidade
     */
    public List<Tecnico> getTecnicosByEspecialidade(String especialidade) {
        logger.debug("Buscando técnicos por especialidade: {}", especialidade);
        return tecnicoDAO.findByEspecialidade(especialidade);
    }

    /**
     * Desativa um técnico
     */
    public void deactivateTecnico(Long tecnicoId) throws Exception {
        logger.info("Desativando técnico ID: {}", tecnicoId);
        Tecnico tecnico = tecnicoDAO.findById(tecnicoId);
        if (tecnico == null) {
            throw new IllegalArgumentException("Técnico não encontrado: " + tecnicoId);
        }
        tecnicoDAO.deactivate(tecnicoId);
        logger.info("Técnico desativado com sucesso: ID {}", tecnicoId);
    }

    /**
     * Deleta um técnico
     */
    public void deleteTecnico(Long tecnicoId) throws Exception {
        logger.info("Deletando técnico ID: {}", tecnicoId);
        Tecnico tecnico = tecnicoDAO.findById(tecnicoId);
        if (tecnico == null) {
            throw new IllegalArgumentException("Técnico não encontrado: " + tecnicoId);
        }
        tecnicoDAO.deleteById(tecnicoId);
        logger.info("Técnico deletado com sucesso: ID {}", tecnicoId);
    }

    /**
     * Conta total de técnicos
     */
    public Long countTecnicos() {
        logger.debug("Contando total de técnicos");
        return tecnicoDAO.count();
    }

    /**
     * Valida técnico
     */
    private void validateTecnico(Tecnico tecnico) throws Exception {
        if (tecnico.getName() == null || tecnico.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do técnico é obrigatório");
        }

        if (tecnico.getCpf() != null && !tecnico.getCpf().isEmpty()) {
            // Validação básica de CPF (pode ser melhorada)
            if (tecnico.getCpf().length() != 11) {
                throw new IllegalArgumentException("CPF deve ter 11 dígitos");
            }
        }

        if (tecnico.getEmail() != null && !tecnico.getEmail().isEmpty()) {
            if (!tecnico.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("Email inválido: " + tecnico.getEmail());
            }
        }
    }
}
