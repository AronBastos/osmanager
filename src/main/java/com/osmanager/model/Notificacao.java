package com.osmanager.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * ==============================================================
 * MONGODB DOCUMENT — INTERVIEW NOTE (Topic: NoSQL / MongoDB)
 * ==============================================================
 * This is NOT a JPA @Entity — it's a Spring Data MongoDB @Document.
 * Key differences from OrdemServico (JPA):
 *
 *   - @Id here is org.springframework.data.annotation.Id (generic Spring
 *     Data), not jakarta.persistence.Id. Mongo generates a String
 *     ObjectId when the field is null on save — no @GeneratedValue needed.
 *   - No schema/migration: the collection is created on first insert,
 *     and documents in the same collection can have different shapes.
 *     Validation that a relational DB enforces at the schema level
 *     (NOT NULL, FK) has to be enforced in application code here.
 *   - No JOINs: ordemServicoId is just a plain Long reference to the
 *     JPA/MySQL OrdemServico aggregate — Mongo doesn't know or care
 *     that it's a foreign key. This is intentional: Notificacao is a
 *     separate, independently-scaling concern (write-heavy, append-only
 *     event log) from the transactional order data.
 *
 * Q: "When would you reach for MongoDB instead of a relational table?"
 * A: When the data is naturally document-shaped, doesn't need
 *    multi-row ACID transactions or JOIN-heavy queries, and you want
 *    schema flexibility or very high write throughput — e.g. logs,
 *    notifications, event streams, audit trails. Here, notifications
 *    about an order never need to be JOINed back into a SQL query
 *    alongside the order itself, so there's no cost to keeping them
 *    in a different store.
 * ==============================================================
 */
@Document(collection = "notificacoes")
public class Notificacao {

    @Id
    private String id;

    @Indexed
    private Long ordemServicoId;

    private Tipo tipo;

    private String mensagem;

    private Instant criadoEm = Instant.now();

    public enum Tipo {
        ABERTURA,
        INICIO,
        CONCLUSAO,
        CANCELAMENTO
    }

    public Notificacao() {}

    public Notificacao(Long ordemServicoId, Tipo tipo, String mensagem) {
        this.ordemServicoId = ordemServicoId;
        this.tipo = tipo;
        this.mensagem = mensagem;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getOrdemServicoId() { return ordemServicoId; }
    public void setOrdemServicoId(Long ordemServicoId) { this.ordemServicoId = ordemServicoId; }

    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public Instant getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Instant criadoEm) { this.criadoEm = criadoEm; }

    @Override
    public String toString() {
        return "Notificacao{id=" + id + ", ordemServicoId=" + ordemServicoId
                + ", tipo=" + tipo + ", mensagem='" + mensagem + "'}";
    }
}
