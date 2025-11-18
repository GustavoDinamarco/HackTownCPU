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

import com.model.domain.Palestrante;
import com.model.services.PalestranteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/palestrantes")
@RequiredArgsConstructor
public class PalestranteController {

    private final PalestranteService palestranteService;

    @GetMapping
    public List<Palestrante> listar() {
        return palestranteService.listarTodos();
    }

    @GetMapping("/{id}")
    public Palestrante buscar(@PathVariable Integer id) {
        return palestranteService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Palestrante criar(@RequestBody Palestrante palestrante) {
        return palestranteService.criar(palestrante);
    }

    @PutMapping("/{id}")
    public Palestrante atualizar(@PathVariable Integer id, @RequestBody Palestrante palestrante) {
        return palestranteService.atualizar(id, palestrante);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Integer id) {
        palestranteService.remover(id);
    }
}