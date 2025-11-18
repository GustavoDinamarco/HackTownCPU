package com.model.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.model.domain.Aluno;
import com.model.repository.AlunoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AlunoService {

    private final AlunoRepository alunoRepository;

    public List<Aluno> listarTodos() {
        return alunoRepository.findAll();
    }

    public Aluno buscarPorId(Integer id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID não pode ser nulo");
        }
        return alunoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));
    }

    public Aluno criar(Aluno aluno) {
        if (aluno.getCpf() != null && alunoRepository.existsByCpf(aluno.getCpf())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF já cadastrado");
        }
        return alunoRepository.save(aluno);
    }

    public Aluno atualizar(Integer id, Aluno alunoAtualizado) {
        Aluno existente = buscarPorId(id);
        existente.setNome(alunoAtualizado.getNome());
        existente.setCpf(alunoAtualizado.getCpf());
        existente.setContato(alunoAtualizado.getContato());
        existente.setEmail(alunoAtualizado.getEmail());
        existente.setDataNascimento(alunoAtualizado.getDataNascimento());
        existente.setCursos(alunoAtualizado.getCursos());
        existente.setPeriodo(alunoAtualizado.getPeriodo());
        return alunoRepository.save(existente);
    }

    public void remover(Integer id) {
        Aluno aluno = buscarPorId(id);
        if (aluno == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado");
        }
        alunoRepository.delete(aluno);
    }
}