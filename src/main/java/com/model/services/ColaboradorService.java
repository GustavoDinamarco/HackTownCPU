package com.model.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.model.domain.Colaborador;
import com.model.repository.ColaboradorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ColaboradorService {

    private final ColaboradorRepository colaboradorRepository;

    public List<Colaborador> listarTodos() {
        return colaboradorRepository.findAll();
    }

    public Colaborador buscarPorId(Integer id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID não pode ser nulo");
        }
        return colaboradorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Colaborador não encontrado"));
    }

    public Colaborador criar(Colaborador colaborador) {
        if (colaborador == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Colaborador não pode ser nulo");
        }
        return colaboradorRepository.save(colaborador);
    }

    public Colaborador atualizar(Integer id, Colaborador colaboradorAtualizado) {
        Colaborador existente = buscarPorId(id);
        existente.setNome(colaboradorAtualizado.getNome());
        existente.setContato(colaboradorAtualizado.getContato());
        existente.setEmail(colaboradorAtualizado.getEmail());
        existente.setCpf(colaboradorAtualizado.getCpf());
        existente.setCargo(colaboradorAtualizado.getCargo());
        return colaboradorRepository.save(existente);
    }

    public void remover(Integer id) {
        Colaborador colaborador = buscarPorId(id);
        if (colaborador == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Colaborador não encontrado");
        }
        colaboradorRepository.delete(colaborador);
    }
}