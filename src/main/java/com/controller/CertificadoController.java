package com.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.model.domain.Certificado;
import com.model.services.CertificadoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/certificados")
@RequiredArgsConstructor
public class CertificadoController {

    private final CertificadoService certificadoService;

    @GetMapping
    public List<Certificado> listarTodos(@RequestParam(required = false) Integer alunoId,
                                         @RequestParam(required = false) Integer eventoId) {
        if (alunoId != null) {
            return certificadoService.listarPorAluno(alunoId);
        }
        if (eventoId != null) {
            return certificadoService.listarPorEvento(eventoId);
        }
        return certificadoService.listarTodos();
    }

    @GetMapping("/hash/{hash}")
    public Certificado buscarPorHash(@PathVariable String hash) {
        return certificadoService.buscarPorHash(hash);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Certificado emitir(@RequestParam Integer alunoId,
                              @RequestParam Integer eventoId,
                              @RequestParam Integer palestranteId,
                              @RequestBody Certificado payload) {
        return certificadoService.emitir(alunoId, eventoId, palestranteId, payload);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Integer id) {
        certificadoService.remover(id);
    }
}