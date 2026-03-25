package com.osmanager.service;

import com.osmanager.dao.ServicoDAO;
import com.osmanager.model.Servico;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

/**
 * Serviço de negócio para Serviço
 */
@Stateless
public class ServicoService {

    private static final Logger logger = LogManager.getLogger(ServicoService.class);

    @Inject
    private ServicoDAO servicoDAO;

    /**
     * Cria um novo serviço
     */
    public Servico createServico(Servico servico) throws Exception {
        logger.info("Criando novo serviço: {}", servico.getNome());

        validateServico(servico);

        servico.setAtivo(true);
        Servico created = servicoDAO.create(servico);
        logger.info("Serviço criado com sucesso: ID {}", created.getId());
        return created;
    }

    /**
     * Atualiza um serviço
     */
    public Servico updateServico(Servico servico) throws Exception {
        logger.info("Atualizando serviço ID: {}", servico.getId());

        if (servico.getId() == null) {
            throw new IllegalArgumentException("ID do serviço é obrigatório");
        }

        Servico existing = servicoDAO.findById(servico.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Serviço não encontrado: " + servico.getId());
        }

        validateServico(servico);

        Servico updated = servicoDAO.update(servico);
        logger.info("Serviço atualizado com sucesso: ID {}", servico.getId());
        return updated;
    }

    /**
     * Busca serviço por ID
     */
    public Servico getServicoById(Long id) {
        logger.debug("Buscando serviço por ID: {}", id);
        return servicoDAO.findById(id);
    }

    /**
     * Lista todos os serviços ativos
     */
    public List<Servico> getAllActiveServicos() {
        logger.debug("Listando todos os serviços ativos");
        return servicoDAO.findAllActive();
    }

    /**
     * Busca serviços por nome
     */
    public List<Servico> searchServicosByNome(String nome) {
        logger.debug("Buscando serviços por nome: {}", nome);
        if (nome == null || nome.trim().isEmpty()) {
            return servicoDAO.findAllActive();
        }
        return servicoDAO.findByNome(nome);
    }

    /**
     * Busca serviços por categoria
     */
    public List<Servico> getServicosByCategoria(String categoria) {
        logger.debug("Buscando serviços por categoria: {}", categoria);
        return servicoDAO.findByCategoria(categoria);
    }

    /**
     * Busca serviços por faixa de preço
     */
    public List<Servico> getServicosByPrecoRange(double minPreco, double maxPreco) {
        logger.debug("Buscando serviços por faixa de preço: {} - {}", minPreco, maxPreco);
        return servicoDAO.findByPrecoRange(minPreco, maxPreco);
    }

    /**
     * Desativa um serviço
     */
    public void deactivateServico(Long servicoId) throws Exception {
        logger.info("Desativando serviço ID: {}", servicoId);
        Servico servico = servicoDAO.findById(servicoId);
        if (servico == null) {
            throw new IllegalArgumentException("Serviço não encontrado: " + servicoId);
        }
        servicoDAO.deactivate(servicoId);
        logger.info("Serviço desativado com sucesso: ID {}", servicoId);
    }

    /**
     * Deleta um serviço
     */
    public void deleteServico(Long servicoId) throws Exception {
        logger.info("Deletando serviço ID: {}", servicoId);
        Servico servico = servicoDAO.findById(servicoId);
        if (servico == null) {
            throw new IllegalArgumentException("Serviço não encontrado: " + servicoId);
        }
        servicoDAO.deleteById(servicoId);
        logger.info("Serviço deletado com sucesso: ID {}", servicoId);
    }

    /**
     * Conta total de serviços
     */
    public Long countServicos() {
        logger.debug("Contando total de serviços");
        return servicoDAO.count();
    }

    /**
     * Valida serviço
     */
    private void validateServico(Servico servico) throws Exception {
        if (servico.getNome() == null || servico.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do serviço é obrigatório");
        }

        if (servico.getPreco() == null || servico.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço do serviço deve ser maior que zero");
        }
    }
}
