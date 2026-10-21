package com.osmanager.repository;

import com.osmanager.model.Notificacao;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * ==============================================================
 * REACTIVE REPOSITORY — INTERVIEW NOTE (Topic: Programação Reativa)
 * ==============================================================
 * Same derived-query-method convention as a normal Spring Data
 * repository (findByXOrderByY), but every method returns a
 * reactive publisher instead of a blocking value:
 *
 *   - Flux<T>  → 0..N results, the reactive equivalent of List<T>.
 *   - Mono<T>  → 0..1 result,  the reactive equivalent of Optional<T>.
 *
 * Nothing happens until something SUBSCRIBES to the returned
 * Flux/Mono — these are lazy, cold publishers by default. The
 * underlying Mongo driver call only fires on subscription, and the
 * calling thread is never blocked waiting for I/O.
 * ==============================================================
 */
public interface NotificacaoReactiveRepository extends ReactiveMongoRepository<Notificacao, String> {

    Flux<Notificacao> findByOrdemServicoIdOrderByCriadoEmDesc(Long ordemServicoId);
}
