package com.model.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.model.domain.Curso;
import com.model.repository.CursoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;

    public List<Curso> listarTodos() {
        return cursoRepository.findAll();
    }

    public Curso buscarPorId(Integer id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado"));
    }

    public Curso criar(Curso curso) {
        if (cursoRepository.existsByNomeIgnoreCase(curso.getNome())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Curso já cadastrado");
        }
        return cursoRepository.save(curso);
    }

    public Curso atualizar(Integer id, Curso cursoAtualizado) {
        Curso existente = buscarPorId(id);
        existente.setNome(cursoAtualizado.getNome());
        return cursoRepository.save(existente);
    }

    public void remover(Integer id) {
        cursoRepository.delete(buscarPorId(id));
    }
}