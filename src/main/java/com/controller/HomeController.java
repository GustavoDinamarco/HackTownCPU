package com.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "API de Eventos - Sistema de Gerenciamento");
        response.put("version", "1.0.0");
        response.put("endpoints", Map.of(
            "eventos", "/api/eventos",
            "cursos", "/api/cursos",
            "alunos", "/api/alunos",
            "palestrantes", "/api/palestrantes",
            "colaboradores", "/api/colaboradores",
            "certificados", "/api/certificados",
            "inscricoes", "/api/inscricoes"
        ));
        return response;
    }
}

