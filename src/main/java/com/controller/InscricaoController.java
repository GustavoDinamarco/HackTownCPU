package com.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.model.domain.Inscricao;
import com.model.services.InscricaoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inscricoes")
@RequiredArgsConstructor
public class InscricaoController {

    private final InscricaoService inscricaoService;

    @GetMapping
    public List<Inscricao> listarPorAluno(@RequestParam Integer alunoId) {
        return inscricaoService.listarPorAluno(alunoId);
    }

    @PutMapping("/{id}/presenca")
    public Inscricao registrarPresenca(@PathVariable Integer id, @RequestParam Boolean presente) {
        return inscricaoService.atualizarPresenca(id, presente);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Inscricao registrar(@RequestParam Integer eventoId, @RequestParam Integer alunoId) {
        return inscricaoService.registrar(eventoId, alunoId);
    }
}