package com.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HomeController.class)
@DisplayName("Testes para HomeController")
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Deve retornar informações da API na raiz")
    void deveRetornarInformacoesDaApiNaRaiz() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.version").exists())
            .andExpect(jsonPath("$.endpoints").exists())
            .andExpect(jsonPath("$.endpoints.eventos").value("/api/eventos"))
            .andExpect(jsonPath("$.endpoints.cursos").value("/api/cursos"))
            .andExpect(jsonPath("$.endpoints.alunos").value("/api/alunos"))
            .andExpect(jsonPath("$.endpoints.palestrantes").value("/api/palestrantes"))
            .andExpect(jsonPath("$.endpoints.colaboradores").value("/api/colaboradores"))
            .andExpect(jsonPath("$.endpoints.certificados").value("/api/certificados"))
            .andExpect(jsonPath("$.endpoints.inscricoes").value("/api/inscricoes"));
    }
}

