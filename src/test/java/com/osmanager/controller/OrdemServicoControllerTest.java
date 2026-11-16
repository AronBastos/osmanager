package com.osmanager.controller;

import com.osmanager.service.OrdemServicoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdemServicoController.class)
class OrdemServicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrdemServicoService ordemServicoService;

    @Test
    @DisplayName("GET /api/orders/count?status=OPEN → 200 com a contagem")
    void countByStatus_ReturnsCount() throws Exception {
        when(ordemServicoService.countByStatus(com.osmanager.model.OrdemServico.Status.OPEN)).thenReturn(3L);

        mockMvc.perform(get("/api/orders/count").param("status", "OPEN"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    @DisplayName("GET /api/orders/count sem 'status' → 400 (e não 500)")
    void countByStatus_MissingParam_Returns400() throws Exception {
        mockMvc.perform(get("/api/orders/count"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Rota inexistente → 404 (e não 500)")
    void unknownRoute_Returns404() throws Exception {
        mockMvc.perform(get("/api/orders/rota/inexistente/xyz"))
                .andExpect(status().isNotFound());
    }
}
