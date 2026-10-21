package com.osmanager.event;

import com.osmanager.model.Notificacao;

/**
 * ==============================================================
 * APPLICATION EVENT — INTERVIEW NOTE (Topic: Arquitetura / Eventos)
 * ==============================================================
 * A plain POJO published through Spring's ApplicationEventPublisher.
 * This is what decouples OrdemServicoService (blocking JPA, MySQL)
 * from NotificacaoService (reactive, MongoDB): the order service
 * knows nothing about notifications, Mongo, or Reactor — it just
 * publishes "this happened". Whoever listens (NotificacaoEventListener)
 * decides what to do with it.
 *
 * Q: "Why not just call notificacaoService.registrar(...) directly
 *     from OrdemServicoService?"
 * A: You could — it would work. But it couples a core business
 *    service to a side-effect (notifications) that has nothing to do
 *    with the order's own invariants. With events, OrdemServicoService
 *    stays focused on the order; adding/removing/changing what reacts
 *    to a status change (notify, audit-log, send email...) never
 *    touches it again. It's the same trade-off microservices make with
 *    a message broker — this is the in-process version of that idea.
 * ==============================================================
 */
public class OrdemServicoStatusChangedEvent {

    private final Long ordemServicoId;
    private final Notificacao.Tipo tipo;
    private final String mensagem;

    public OrdemServicoStatusChangedEvent(Long ordemServicoId, Notificacao.Tipo tipo, String mensagem) {
        this.ordemServicoId = ordemServicoId;
        this.tipo = tipo;
        this.mensagem = mensagem;
    }

    public Long getOrdemServicoId() { return ordemServicoId; }
    public Notificacao.Tipo getTipo() { return tipo; }
    public String getMensagem() { return mensagem; }
}
