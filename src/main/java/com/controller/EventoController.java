package com.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.model.domain.Evento;
import com.model.domain.Inscricao;
import com.model.services.EventoService;
import com.model.services.InscricaoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;
    private final InscricaoService inscricaoService;

    @GetMapping
    public List<Evento> listar(@RequestParam(value = "categoria", required = false) String categoria) {
        return eventoService.buscarPorCategoria(categoria);
    }

    @GetMapping("/{id}")
    public Evento buscar(@PathVariable Integer id) {
        return eventoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Evento criar(@RequestBody Evento evento) {
        return eventoService.criar(evento);
    }

    @PutMapping("/{id}")
    public Evento atualizar(@PathVariable Integer id, @RequestBody Evento evento) {
        return eventoService.atualizar(id, evento);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Integer id) {
        eventoService.remover(id);
    }

    @PostMapping("/{eventoId}/inscricoes")
    @ResponseStatus(HttpStatus.CREATED)
    public Inscricao inscrever(@PathVariable Integer eventoId, @RequestParam Integer alunoId) {
        return inscricaoService.registrar(eventoId, alunoId);
    }

    @GetMapping("/{eventoId}/inscricoes")
    public List<Inscricao> listarInscricoes(@PathVariable Integer eventoId) {
        return inscricaoService.listarPorEvento(eventoId);
    }
}