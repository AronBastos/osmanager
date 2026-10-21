package com.osmanager.event;

import com.osmanager.service.NotificacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * ==============================================================
 * BRIDGING BLOCKING → REACTIVE — INTERVIEW NOTE
 * ==============================================================
 * This is where the blocking JPA world (OrdemServicoService, inside
 * a @Transactional MySQL transaction) hands off to the reactive
 * MongoDB world (NotificacaoService).
 *
 * @TransactionalEventListener(phase = AFTER_COMMIT)
 *   The event fires ONLY after the surrounding @Transactional method
 *   in OrdemServicoService COMMITS successfully. If startOrder()
 *   throws and rolls back, no notification is ever written — we never
 *   notify about a status change that didn't actually happen.
 *   (Default phase, plain @EventListener, would fire even if the
 *   transaction later rolled back — a classic source of "phantom"
 *   side effects.)
 *
 * .subscribe(...) at the end of onStatusChanged
 *   Mono/Flux are lazy — registrar(...) alone does NOTHING. Calling
 *   subscribe() is what actually triggers the Mongo write. This is a
 *   deliberate "fire and forget": the listener runs on the thread
 *   that just committed the SQL transaction, and we don't want it
 *   blocked waiting for Mongo. We give subscribe() an error consumer
 *   explicitly — a bare subscribe() with no error handler silently
 *   drops exceptions (Reactor logs them via Hooks.onErrorDropped and
 *   moves on), which is almost never what you want in production code.
 * ==============================================================
 */
@Component
public class NotificacaoEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoEventListener.class);

    private final NotificacaoService notificacaoService;

    public NotificacaoEventListener(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onStatusChanged(OrdemServicoStatusChangedEvent event) {
        notificacaoService.registrar(event.getOrdemServicoId(), event.getTipo(), event.getMensagem())
                .subscribe(
                        notificacao -> log.debug("Notificacao registrada: {}", notificacao),
                        error -> log.error("Falha ao registrar notificacao para ordem {}: {}",
                                event.getOrdemServicoId(), error.getMessage(), error)
                );
    }
}
