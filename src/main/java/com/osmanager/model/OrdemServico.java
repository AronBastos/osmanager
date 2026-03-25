package com.osmanager.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade JPA: Ordem de Serviço
 * Representa uma ordem de serviço da oficina
 */
@Entity
@Table(name = "ordens_servico", indexes = {
    @Index(name = "idx_cliente", columnList = "cliente_id"),
    @Index(name = "idx_tecnico", columnList = "tecnico_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_data_abertura", columnList = "data_abertura")
})
public class OrdemServico implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id", nullable = false)
    private Tecnico tecnico;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura;

    @Column(name = "data_fechamento")
    private LocalDateTime dataFechamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusOrdemServico status = StatusOrdemServico.ABERTA;

    @Column(name = "descricao", length = 1000)
    private String descricao;

    @Column(name = "observacoes", length = 1000)
    private String observacoes;

    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "desconto", precision = 10, scale = 2)
    private BigDecimal desconto = BigDecimal.ZERO;

    @Column(name = "valor_final", precision = 10, scale = 2)
    private BigDecimal valorFinal = BigDecimal.ZERO;

    @PrePersist
    protected void prePersist() {
        if (dataAbertura == null) {
            dataAbertura = LocalDateTime.now();
        }
        calcularValorFinal();
    }

    @PreUpdate
    protected void preUpdate() {
        calcularValorFinal();
    }

    private void calcularValorFinal() {
        if (valorTotal != null && desconto != null) {
            valorFinal = valorTotal.subtract(desconto);
        }
    }

    // Enum para status
    public enum StatusOrdemServico {
        ABERTA,
        EM_ANDAMENTO,
        FINALIZADA,
        CANCELADA
    }

    // Construtores
    public OrdemServico() {
    }

    public OrdemServico(Cliente cliente, Tecnico tecnico, String descricao) {
        this.cliente = cliente;
        this.tecnico = tecnico;
        this.descricao = descricao;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Tecnico getTecnico() {
        return tecnico;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public StatusOrdemServico getStatus() {
        return status;
    }

    public void setStatus(StatusOrdemServico status) {
        this.status = status;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    @Override
    public String toString() {
        return "OrdemServico{" +
                "id=" + id +
                ", cliente=" + (cliente != null ? cliente.getName() : null) +
                ", tecnico=" + (tecnico != null ? tecnico.getName() : null) +
                ", status=" + status +
                ", valorTotal=" + valorTotal +
                '}';
    }
}
