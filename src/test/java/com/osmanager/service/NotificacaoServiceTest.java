package com.osmanager.service;

import com.osmanager.model.Notificacao;
import com.osmanager.repository.NotificacaoReactiveRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * ==============================================================
 * TESTING REACTIVE CODE — INTERVIEW NOTE (Topic: Programação Reativa)
 * ==============================================================
 * You can't just call a method and assert the return value like with
 * blocking code — a Mono/Flux is a RECIPE for a computation, not the
 * result itself. StepVerifier subscribes to the publisher and lets you
 * assert, in order, each signal it emits (onNext/onComplete/onError).
 *
 * No Spring context is loaded here — NotificacaoReactiveRepository is
 * a plain Mockito @Mock, same pattern as ClientServiceTest, just with
 * Mono/Flux return values instead of Optional/List.
 * ==============================================================
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NotificacaoService Unit Tests")
class NotificacaoServiceTest {

    @Mock
    private NotificacaoReactiveRepository repository;

    @InjectMocks
    private NotificacaoService notificacaoService;

    @Test
    @DisplayName("Should save and emit the notification when registrar() is subscribed to")
    void shouldRegistrarNotificacao() {
        Notificacao saved = new Notificacao(1L, Notificacao.Tipo.ABERTURA, "Ordem aberta");
        saved.setId("abc123");

        when(repository.save(any(Notificacao.class))).thenReturn(Mono.just(saved));

        // StepVerifier.create(...) does NOT run anything by itself — it only
        // runs once .verifyComplete()/.verify() triggers the subscription.
        StepVerifier.create(notificacaoService.registrar(1L, Notificacao.Tipo.ABERTURA, "Ordem aberta"))
                .expectNextMatches(n -> n.getId().equals("abc123") && n.getOrdemServicoId().equals(1L))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should list notification history for an order in repository order")
    void shouldListarPorOrdemServico() {
        Notificacao n1 = new Notificacao(1L, Notificacao.Tipo.CONCLUSAO, "Concluída");
        Notificacao n2 = new Notificacao(1L, Notificacao.Tipo.ABERTURA, "Aberta");

        when(repository.findByOrdemServicoIdOrderByCriadoEmDesc(1L)).thenReturn(Flux.just(n1, n2));

        StepVerifier.create(notificacaoService.listarPorOrdemServico(1L))
                .expectNext(n1, n2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return an empty Flux, not null, when an order has no notifications")
    void shouldReturnEmptyFlux_WhenNoNotifications() {
        when(repository.findByOrdemServicoIdOrderByCriadoEmDesc(99L)).thenReturn(Flux.empty());

        StepVerifier.create(notificacaoService.listarPorOrdemServico(99L))
                .verifyComplete(); // no expectNext — zero elements, then completes
    }

    @Test
    @DisplayName("Should only push notifications matching the subscribed order on the live stream")
    void shouldFilterStreamByOrdemServico() {
        Notificacao paraOrdem1 = new Notificacao(1L, Notificacao.Tipo.INICIO, "Para ordem 1");
        Notificacao paraOrdem2 = new Notificacao(2L, Notificacao.Tipo.INICIO, "Para ordem 2");

        when(repository.save(any(Notificacao.class)))
                .thenReturn(Mono.just(paraOrdem1))
                .thenReturn(Mono.just(paraOrdem2));

        // Subscribe to the live stream for order 1 FIRST — Sinks.Many is a hot
        // publisher, it only delivers events emitted AFTER subscription.
        Flux<Notificacao> streamOrdem1 = notificacaoService.streamPorOrdemServico(1L);

        StepVerifier.create(streamOrdem1)
                .then(() -> {
                    // registrar() alone does nothing until subscribed — fire both here
                    notificacaoService.registrar(1L, Notificacao.Tipo.INICIO, "Para ordem 1").subscribe();
                    notificacaoService.registrar(2L, Notificacao.Tipo.INICIO, "Para ordem 2").subscribe();
                })
                .expectNextMatches(n -> n.getOrdemServicoId().equals(1L))
                .thenCancel() // the stream is infinite — cancel instead of waiting for completion
                .verify(Duration.ofSeconds(2));
    }
}
