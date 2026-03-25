package com.osmanager.service;

import com.osmanager.dao.ClienteDAO;
import com.osmanager.dao.OrdemServicoDAO;
import com.osmanager.dao.TecnicoDAO;
import com.osmanager.model.Cliente;
import com.osmanager.model.OrdemServico;
import com.osmanager.model.OrdemServico.StatusOrdemServico;
import com.osmanager.model.Tecnico;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço de negócio para Ordem de Serviço
 */
@Stateless
public class OrdemServicoService {

    private static final Logger logger = LogManager.getLogger(OrdemServicoService.class);

    @Inject
    private OrdemServicoDAO ordemServicoDAO;

    @Inject
    private ClienteDAO clienteDAO;

    @Inject
    private TecnicoDAO tecnicoDAO;

    /**
     * Cria uma nova ordem de serviço
     */
    public OrdemServico createOrdemServico(OrdemServico ordemServico) throws Exception {
        logger.info("Criando nova ordem de serviço para cliente: {}", ordemServico.getCliente().getName());

        validateOrdemServico(ordemServico);

        ordemServico.setStatus(StatusOrdemServico.ABERTA);
        ordemServico.setDataAbertura(LocalDateTime.now());

        OrdemServico created = ordemServicoDAO.create(ordemServico);
        logger.info("Ordem de serviço criada com sucesso: ID {}", created.getId());
        return created;
    }

    /**
     * Atualiza uma ordem de serviço
     */
    public OrdemServico updateOrdemServico(OrdemServico ordemServico) throws Exception {
        logger.info("Atualizando ordem de serviço ID: {}", ordemServico.getId());

        if (ordemServico.getId() == null) {
            throw new IllegalArgumentException("ID da ordem de serviço é obrigatório");
        }

        OrdemServico existing = ordemServicoDAO.findById(ordemServico.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Ordem de serviço não encontrada: " + ordemServico.getId());
        }

        validateOrdemServico(ordemServico);

        OrdemServico updated = ordemServicoDAO.update(ordemServico);
        logger.info("Ordem de serviço atualizada com sucesso: ID {}", ordemServico.getId());
        return updated;
    }

    /**
     * Finaliza uma ordem de serviço
     */
    public OrdemServico finalizarOrdemServico(Long ordemServicoId) throws Exception {
        logger.info("Finalizando ordem de serviço ID: {}", ordemServicoId);

        OrdemServico ordemServico = ordemServicoDAO.findById(ordemServicoId);
        if (ordemServico == null) {
            throw new IllegalArgumentException("Ordem de serviço não encontrada: " + ordemServicoId);
        }

        if (ordemServico.getStatus() == StatusOrdemServico.FINALIZADA) {
            throw new IllegalArgumentException("Ordem de serviço já está finalizada");
        }

        ordemServico.setStatus(StatusOrdemServico.FINALIZADA);
        ordemServico.setDataFechamento(LocalDateTime.now());

        OrdemServico updated = ordemServicoDAO.update(ordemServico);
        logger.info("Ordem de serviço finalizada com sucesso: ID {}", ordemServicoId);
        return updated;
    }

    /**
     * Cancela uma ordem de serviço
     */
    public OrdemServico cancelarOrdemServico(Long ordemServicoId) throws Exception {
        logger.info("Cancelando ordem de serviço ID: {}", ordemServicoId);

        OrdemServico ordemServico = ordemServicoDAO.findById(ordemServicoId);
        if (ordemServico == null) {
            throw new IllegalArgumentException("Ordem de serviço não encontrada: " + ordemServicoId);
        }

        if (ordemServico.getStatus() == StatusOrdemServico.FINALIZADA) {
            throw new IllegalArgumentException("Não é possível cancelar uma ordem finalizada");
        }

        ordemServico.setStatus(StatusOrdemServico.CANCELADA);

        OrdemServico updated = ordemServicoDAO.update(ordemServico);
        logger.info("Ordem de serviço cancelada com sucesso: ID {}", ordemServicoId);
        return updated;
    }

    /**
     * Busca ordem de serviço por ID
     */
    public OrdemServico getOrdemServicoById(Long id) {
        logger.debug("Buscando ordem de serviço por ID: {}", id);
        return ordemServicoDAO.findById(id);
    }

    /**
     * Lista ordens de serviço por cliente
     */
    public List<OrdemServico> getOrdensByCliente(Long clienteId) {
        logger.debug("Buscando ordens de serviço por cliente ID: {}", clienteId);
        return ordemServicoDAO.findByCliente(clienteId);
    }

    /**
     * Lista ordens de serviço por técnico
     */
    public List<OrdemServico> getOrdensByTecnico(Long tecnicoId) {
        logger.debug("Buscando ordens de serviço por técnico ID: {}", tecnicoId);
        return ordemServicoDAO.findByTecnico(tecnicoId);
    }

    /**
     * Lista ordens de serviço abertas
     */
    public List<OrdemServico> getOrdensAbertas() {
        logger.debug("Listando ordens de serviço abertas");
        return ordemServicoDAO.findOpenOrders();
    }

    /**
     * Lista ordens de serviço em andamento
     */
    public List<OrdemServico> getOrdensEmAndamento() {
        logger.debug("Listando ordens de serviço em andamento");
        return ordemServicoDAO.findInProgressOrders();
    }

    /**
     * Conta ordens por status
     */
    public Long countOrdensByStatus(StatusOrdemServico status) {
        logger.debug("Contando ordens de serviço por status: {}", status);
        return ordemServicoDAO.countByStatus(status);
    }

    /**
     * Valida ordem de serviço
     */
    private void validateOrdemServico(OrdemServico ordemServico) throws Exception {
        if (ordemServico.getCliente() == null || ordemServico.getCliente().getId() == null) {
            throw new IllegalArgumentException("Cliente é obrigatório");
        }

        Cliente cliente = clienteDAO.findById(ordemServico.getCliente().getId());
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado: " + ordemServico.getCliente().getId());
        }

        if (ordemServico.getTecnico() == null || ordemServico.getTecnico().getId() == null) {
            throw new IllegalArgumentException("Técnico é obrigatório");
        }

        Tecnico tecnico = tecnicoDAO.findById(ordemServico.getTecnico().getId());
        if (tecnico == null) {
            throw new IllegalArgumentException("Técnico não encontrado: " + ordemServico.getTecnico().getId());
        }

        if (ordemServico.getDescricao() == null || ordemServico.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
    }
}
