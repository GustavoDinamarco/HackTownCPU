package com.model.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.model.domain.Palestrante;
import com.model.repository.PalestranteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PalestranteService {

    private final PalestranteRepository palestranteRepository;

    public List<Palestrante> listarTodos() {
        return palestranteRepository.findAll();
    }

    public Palestrante buscarPorId(Integer id) {
        return palestranteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Palestrante não encontrado"));
    }

    public Palestrante criar(Palestrante palestrante) {
        if (palestrante.getEmail() != null && palestranteRepository.existsByEmail(palestrante.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail já cadastrado");
        }
        return palestranteRepository.save(palestrante);
    }

    public Palestrante atualizar(Integer id, Palestrante atualizado) {
        Palestrante existente = buscarPorId(id);
        existente.setNome(atualizado.getNome());
        existente.setEmail(atualizado.getEmail());
        existente.setDescricao(atualizado.getDescricao());
        existente.setFotoUrl(atualizado.getFotoUrl());
        existente.setContato(atualizado.getContato());
        return palestranteRepository.save(existente);
    }

    public void remover(Integer id) {
        palestranteRepository.delete(buscarPorId(id));
    }
}