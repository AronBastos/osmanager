package com.osmanager.service;

import com.osmanager.dao.PecaDAO;
import com.osmanager.model.Peca;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

/**
 * Serviço de negócio para Peça
 */
@Stateless
public class PecaService {

    private static final Logger logger = LogManager.getLogger(PecaService.class);

    @Inject
    private PecaDAO pecaDAO;

    /**
     * Cria uma nova peça
     */
    public Peca createPeca(Peca peca) throws Exception {
        logger.info("Criando nova peça: {}", peca.getNome());

        validatePeca(peca);

        // Verifica se código já existe
        if (peca.getCodigo() != null && !peca.getCodigo().isEmpty()) {
            Peca existing = pecaDAO.findByCodigo(peca.getCodigo());
            if (existing != null) {
                throw new IllegalArgumentException("Código de peça já cadastrado: " + peca.getCodigo());
            }
        }

        peca.setAtivo(true);
        Peca created = pecaDAO.create(peca);
        logger.info("Peça criada com sucesso: ID {}", created.getId());
        return created;
    }

    /**
     * Atualiza uma peça
     */
    public Peca updatePeca(Peca peca) throws Exception {
        logger.info("Atualizando peça ID: {}", peca.getId());

        if (peca.getId() == null) {
            throw new IllegalArgumentException("ID da peça é obrigatório");
        }

        Peca existing = pecaDAO.findById(peca.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Peça não encontrada: " + peca.getId());
        }

        // Verifica código único se foi alterado
        if (peca.getCodigo() != null && !peca.getCodigo().equals(existing.getCodigo())) {
            Peca codigoExisting = pecaDAO.findByCodigo(peca.getCodigo());
            if (codigoExisting != null) {
                throw new IllegalArgumentException("Código de peça já cadastrado: " + peca.getCodigo());
            }
        }

        validatePeca(peca);

        Peca updated = pecaDAO.update(peca);
        logger.info("Peça atualizada com sucesso: ID {}", peca.getId());
        return updated;
    }

    /**
     * Busca peça por ID
     */
    public Peca getPecaById(Long id) {
        logger.debug("Buscando peça por ID: {}", id);
        return pecaDAO.findById(id);
    }

    /**
     * Busca peça por código
     */
    public Peca getPecaByCodigo(String codigo) {
        logger.debug("Buscando peça por código: {}", codigo);
        return pecaDAO.findByCodigo(codigo);
    }

    /**
     * Lista todas as peças ativas
     */
    public List<Peca> getAllActivePecas() {
        logger.debug("Listando todas as peças ativas");
        return pecaDAO.findAllActive();
    }

    /**
     * Busca peças por nome
     */
    public List<Peca> searchPecasByNome(String nome) {
        logger.debug("Buscando peças por nome: {}", nome);
        if (nome == null || nome.trim().isEmpty()) {
            return pecaDAO.findAllActive();
        }
        return pecaDAO.findByNome(nome);
    }

    /**
     * Busca peças por categoria
     */
    public List<Peca> getPecasByCategoria(String categoria) {
        logger.debug("Buscando peças por categoria: {}", categoria);
        return pecaDAO.findByCategoria(categoria);
    }

    /**
     * Busca peças com estoque baixo
     */
    public List<Peca> getPecasEstoqueBaixo() {
        logger.debug("Buscando peças com estoque baixo");
        return pecaDAO.findLowStock();
    }

    /**
     * Busca peças por marca
     */
    public List<Peca> getPecasByMarca(String marca) {
        logger.debug("Buscando peças por marca: {}", marca);
        return pecaDAO.findByMarca(marca);
    }

    /**
     * Busca peças por faixa de preço
     */
    public List<Peca> getPecasByPrecoRange(double minPreco, double maxPreco) {
        logger.debug("Buscando peças por faixa de preço: {} - {}", minPreco, maxPreco);
        return pecaDAO.findByPrecoRange(minPreco, maxPreco);
    }

    /**
     * Atualiza quantidade em estoque
     */
    public void updateEstoque(Long pecaId, int quantidade) throws Exception {
        logger.info("Atualizando estoque da peça ID {} para quantidade: {}", pecaId, quantidade);

        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa");
        }

        Peca peca = pecaDAO.findById(pecaId);
        if (peca == null) {
            throw new IllegalArgumentException("Peça não encontrada: " + pecaId);
        }

        pecaDAO.updateStock(pecaId, quantidade);
        logger.info("Estoque atualizado com sucesso para peça ID: {}", pecaId);
    }

    /**
     * Desativa uma peça
     */
    public void deactivatePeca(Long pecaId) throws Exception {
        logger.info("Desativando peça ID: {}", pecaId);
        Peca peca = pecaDAO.findById(pecaId);
        if (peca == null) {
            throw new IllegalArgumentException("Peça não encontrada: " + pecaId);
        }
        pecaDAO.deactivate(pecaId);
        logger.info("Peça desativada com sucesso: ID {}", pecaId);
    }

    /**
     * Deleta uma peça
     */
    public void deletePeca(Long pecaId) throws Exception {
        logger.info("Deletando peça ID: {}", pecaId);
        Peca peca = pecaDAO.findById(pecaId);
        if (peca == null) {
            throw new IllegalArgumentException("Peça não encontrada: " + pecaId);
        }
        pecaDAO.deleteById(pecaId);
        logger.info("Peça deletada com sucesso: ID {}", pecaId);
    }

    /**
     * Conta total de peças
     */
    public Long countPecas() {
        logger.debug("Contando total de peças");
        return pecaDAO.count();
    }

    /**
     * Valida peça
     */
    private void validatePeca(Peca peca) throws Exception {
        if (peca.getNome() == null || peca.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da peça é obrigatório");
        }

        if (peca.getPrecoVenda() == null || peca.getPrecoVenda().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço de venda deve ser maior que zero");
        }

        if (peca.getQuantidadeEstoque() == null || peca.getQuantidadeEstoque() < 0) {
            throw new IllegalArgumentException("Quantidade em estoque não pode ser negativa");
        }
    }
}
