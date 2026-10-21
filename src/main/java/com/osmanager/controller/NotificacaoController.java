package com.osmanager.controller;

import com.osmanager.model.Notificacao;
import com.osmanager.service.NotificacaoService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * ==============================================================
 * REACTIVE CONTROLLER — INTERVIEW NOTE (Topic: Programação Reativa)
 * ==============================================================
 * Base URL: /api/orders/{ordemServicoId}/notifications
 *
 * Endpoints:
 *   GET /api/orders/{id}/notifications         → history, as a JSON array
 *   GET /api/orders/{id}/notifications/stream  → live feed, as SSE
 *
 * This is still a servlet-stack @RestController (the app runs on Tomcat,
 * not Netty — see the pom.xml note on starter-web vs starter-webflux).
 * Returning Flux<Notificacao> from an MVC controller still works:
 * Spring adapts the reactive return type under the hood instead of
 * requiring the whole server to be reactive.
 *
 * The /stream endpoint sets produces = TEXT_EVENT_STREAM_VALUE
 * (Server-Sent Events). The HTTP response never "completes" in the
 * normal sense — the connection stays open and the server pushes a
 * new `data: {...}` line every time NotificacaoService's Sinks.Many
 * emits a matching notification. The client (browser EventSource,
 * curl --no-buffer, etc.) sees each JSON document arrive as soon as
 * it's produced, with no polling.
 * ==============================================================
 */
@RestController
@RequestMapping("/api/orders/{ordemServicoId}/notifications")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    /** GET /api/orders/{id}/notifications — notification history for an order. */
    @GetMapping
    public Flux<Notificacao> listar(@PathVariable Long ordemServicoId) {
        return notificacaoService.listarPorOrdemServico(ordemServicoId);
    }

    /** GET /api/orders/{id}/notifications/stream — live SSE feed for an order. */
    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Notificacao> stream(@PathVariable Long ordemServicoId) {
        return notificacaoService.streamPorOrdemServico(ordemServicoId);
    }
}
