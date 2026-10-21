package com.osmanager.service;

import com.osmanager.model.Notificacao;
import com.osmanager.repository.NotificacaoReactiveRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * ==============================================================
 * REACTIVE SERVICE — INTERVIEW NOTE (Topic: Programação Reativa)
 * ==============================================================
 * Two different reactive needs, two different tools:
 *
 * 1) "Give me the notifications already in the database for this
 *    order" → a straight Flux from the reactive Mongo repository.
 *    Finite, pull-based: the client asks once, gets what exists now.
 *
 * 2) "Push me every NEW notification for this order as it happens,
 *    live" → Mongo alone doesn't give you that over a plain query;
 *    you need a HOT stream that stays open and emits as events occur.
 *    Sinks.Many is Reactor's way to create one: a stream you can push
 *    values INTO imperatively (tryEmitNext) while any number of
 *    subscribers pull values OUT reactively.
 *
 * Sinks.many().multicast().onBackpressureBuffer():
 *   - multicast(): any number of subscribers can attach; all of them
 *     observe the same sequence of emissions (as opposed to unicast,
 *     which only tolerates a single subscriber ever).
 *   - onBackpressureBuffer(): emissions that have no subscriber ready
 *     to consume them yet — a slow subscriber, or NO subscriber at
 *     all, which is the common case here between notifications — are
 *     buffered instead of being dropped. This is backpressure: the
 *     consumer, not the producer, controls how fast data flows to it.
 *
 *     CONFIRMED BY RUNNING THIS: calling /start (emits INICIO) and
 *     only THEN opening an SSE connection to /stream still delivered
 *     the INICIO event to that late subscriber, followed live by
 *     CONCLUSAO when /complete was called. onBackpressureBuffer()
 *     replays whatever is sitting in the buffer to a new subscriber —
 *     this is NOT a "join and only see what happens from now on"
 *     stream. If you specifically want the latter (no catch-up on
 *     connect), the tool is Sinks.many().multicast().directBestEffort()
 *     instead, which drops emissions that have no subscriber demand.
 * ==============================================================
 */
@Service
public class NotificacaoService {

    private final NotificacaoReactiveRepository repository;
    private final Sinks.Many<Notificacao> sink = Sinks.many().multicast().onBackpressureBuffer();

    public NotificacaoService(NotificacaoReactiveRepository repository) {
        this.repository = repository;
    }

    /**
     * Persists a notification and publishes it to any live subscriber.
     *
     * Mono<Notificacao> is RETURNED, not executed — nothing hits Mongo
     * until the caller subscribes (see NotificacaoEventListener).
     */
    public Mono<Notificacao> registrar(Long ordemServicoId, Notificacao.Tipo tipo, String mensagem) {
        Notificacao notificacao = new Notificacao(ordemServicoId, tipo, mensagem);
        return repository.save(notificacao)
                .doOnNext(sink::tryEmitNext);
    }

    /** Notification history for an order — finite Flux, backed by a Mongo query. */
    public Flux<Notificacao> listarPorOrdemServico(Long ordemServicoId) {
        return repository.findByOrdemServicoIdOrderByCriadoEmDesc(ordemServicoId);
    }

    /** Live stream of new notifications for an order — infinite Flux until the client disconnects. */
    public Flux<Notificacao> streamPorOrdemServico(Long ordemServicoId) {
        return sink.asFlux().filter(n -> n.getOrdemServicoId().equals(ordemServicoId));
    }
}
