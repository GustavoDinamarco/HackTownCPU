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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.model.domain.Colaborador;
import com.model.services.ColaboradorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/colaboradores")
@RequiredArgsConstructor
public class ColaboradorController {

    private final ColaboradorService colaboradorService;

    @GetMapping
    public List<Colaborador> listar() {
        return colaboradorService.listarTodos();
    }

    @GetMapping("/{id}")
    public Colaborador buscar(@PathVariable Integer id) {
        return colaboradorService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Colaborador criar(@RequestBody Colaborador colaborador) {
        return colaboradorService.criar(colaborador);
    }

    @PutMapping("/{id}")
    public Colaborador atualizar(@PathVariable Integer id, @RequestBody Colaborador colaborador) {
        return colaboradorService.atualizar(id, colaborador);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Integer id) {
        colaboradorService.remover(id);
    }
}