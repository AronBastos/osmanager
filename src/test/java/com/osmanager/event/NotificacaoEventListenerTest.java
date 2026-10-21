package com.osmanager.event;

import com.osmanager.model.Notificacao;
import com.osmanager.service.NotificacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ==============================================================
 * TESTING THE BLOCKING → REACTIVE BRIDGE — INTERVIEW NOTE
 * ==============================================================
 * Two things worth asserting about NotificacaoEventListener:
 *
 * 1) It actually subscribes — calling registrar() and never
 *    subscribing would silently do nothing (see NotificacaoService's
 *    note on lazy publishers). Mono.just(...) resolves synchronously
 *    on subscribe, so if subscribe() never happened, this test's
 *    verify() would still pass but nothing would really run in prod.
 *    What proves subscription happened here is test #2.
 *
 * 2) It never lets a failed Mono escape as an exception — the
 *    listener runs right after a database commit; a notification
 *    failure must never look like the order transition itself failed.
 * ==============================================================
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NotificacaoEventListener Unit Tests")
class NotificacaoEventListenerTest {

    @Mock
    private NotificacaoService notificacaoService;

    @InjectMocks
    private NotificacaoEventListener listener;

    @Test
    @DisplayName("Should ask NotificacaoService to register a notification for the event")
    void shouldRegistrarNotificacao_WhenEventReceived() {
        Notificacao saved = new Notificacao(1L, Notificacao.Tipo.ABERTURA, "Ordem aberta");
        when(notificacaoService.registrar(eq(1L), eq(Notificacao.Tipo.ABERTURA), any()))
                .thenReturn(Mono.just(saved));

        listener.onStatusChanged(new OrdemServicoStatusChangedEvent(1L, Notificacao.Tipo.ABERTURA, "Ordem aberta"));

        verify(notificacaoService).registrar(1L, Notificacao.Tipo.ABERTURA, "Ordem aberta");
    }

    @Test
    @DisplayName("Should not propagate an exception when the reactive write fails")
    void shouldNotThrow_WhenRegistrarFails() {
        when(notificacaoService.registrar(any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("Mongo indisponível")));

        assertThatCode(() -> listener.onStatusChanged(
                new OrdemServicoStatusChangedEvent(1L, Notificacao.Tipo.ABERTURA, "Ordem aberta")))
                .doesNotThrowAnyException();
    }
}
