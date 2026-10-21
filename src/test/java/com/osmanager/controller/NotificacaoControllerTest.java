package com.osmanager.controller;

import com.osmanager.model.Notificacao;
import com.osmanager.service.NotificacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ==============================================================
 * TESTING A REACTIVE CONTROLLER WITH MockMvc — INTERVIEW NOTE
 * ==============================================================
 * NotificacaoController runs on the SERVLET stack (Tomcat), not on
 * Netty/WebFlux — see the pom.xml note. When an MVC @RestController
 * method returns Mono/Flux, Spring MVC processes the request
 * ASYNCHRONOUSLY: the first dispatch only STARTS the async handling
 * (request().asyncStarted()), and you have to perform a second,
 * explicit asyncDispatch(mvcResult) to get the final HTTP response.
 *
 * (If this were a true WebFlux app — Netty, no starter-web — the
 * test would use WebTestClient instead of MockMvc, and wouldn't need
 * this two-step dance.)
 * ==============================================================
 */
@WebMvcTest(NotificacaoController.class)
@DisplayName("NotificacaoController @WebMvcTest Tests")
class NotificacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificacaoService notificacaoService;

    private Notificacao sample;

    @BeforeEach
    void setUp() {
        sample = new Notificacao(1L, Notificacao.Tipo.ABERTURA, "Ordem de serviço aberta");
        sample.setId("abc123");
    }

    @Test
    @DisplayName("GET /api/orders/{id}/notifications — should return 200 with notification history")
    void listar_Returns200WithHistory() throws Exception {
        when(notificacaoService.listarPorOrdemServico(1L)).thenReturn(Flux.just(sample));

        MvcResult mvcResult = mockMvc.perform(get("/api/orders/1/notifications"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("abc123"))
                .andExpect(jsonPath("$[0].tipo").value("ABERTURA"));
    }

    @Test
    @DisplayName("GET /api/orders/{id}/notifications — should return an empty array when there's no history")
    void listar_Returns200WithEmptyArray_WhenNoHistory() throws Exception {
        when(notificacaoService.listarPorOrdemServico(2L)).thenReturn(Flux.empty());

        MvcResult mvcResult = mockMvc.perform(get("/api/orders/2/notifications"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/orders/{id}/notifications/stream — should respond with text/event-stream")
    void stream_RespondsWithServerSentEventsContentType() throws Exception {
        // A real stream never completes; a bounded Flux is enough to exercise
        // the endpoint's wiring and content negotiation in this test.
        when(notificacaoService.streamPorOrdemServico(1L)).thenReturn(Flux.just(sample));

        MvcResult mvcResult = mockMvc.perform(get("/api/orders/1/notifications/stream"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM));
    }
}
