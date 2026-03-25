package com.osmanager.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Entidade JPA: Serviço
 * Representa um serviço oferecido pela oficina
 */
@Entity
@Table(name = "servicos", indexes = {
    @Index(name = "idx_nome_servico", columnList = "nome"),
    @Index(name = "idx_categoria", columnList = "categoria")
})
public class Servico implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", length = 150, nullable = false)
    private String nome;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @Column(name = "categoria", length = 100)
    private String categoria;

    @Column(name = "preco", precision = 10, scale = 2, nullable = false)
    private BigDecimal preco;

    @Column(name = "tempo_estimado_horas")
    private Integer tempoEstimadoHoras;

    @Column(name = "ativo")
    private Boolean ativo = true;

    // Construtores
    public Servico() {
    }

    public Servico(String nome, String descricao, BigDecimal preco) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Integer getTempoEstimadoHoras() {
        return tempoEstimadoHoras;
    }

    public void setTempoEstimadoHoras(Integer tempoEstimadoHoras) {
        this.tempoEstimadoHoras = tempoEstimadoHoras;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "Servico{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", preco=" + preco +
                '}';
    }
}
