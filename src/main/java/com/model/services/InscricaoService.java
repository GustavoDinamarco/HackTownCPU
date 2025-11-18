package com.model.services;

import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.model.domain.Aluno;
import com.model.domain.Evento;
import com.model.domain.Inscricao;
import com.model.repository.AlunoRepository;
import com.model.repository.EventoRepository;
import com.model.repository.InscricaoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InscricaoService {

    private final InscricaoRepository inscricaoRepository;
    private final EventoRepository eventoRepository;
    private final AlunoRepository alunoRepository;

    public List<Inscricao> listarPorEvento(Integer eventoId) {
        return inscricaoRepository.findByEventoId(eventoId);
    }

    public List<Inscricao> listarPorAluno(Integer alunoId) {
        return inscricaoRepository.findByAlunoId(alunoId);
    }

    public Inscricao registrar(Integer eventoId, Integer alunoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));

        if (inscricaoRepository.existsByEventoIdAndAlunoId(eventoId, alunoId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Aluno já inscrito neste evento");
        }
        if (!evento.temVagasDisponiveis()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evento sem vagas disponíveis");
        }
        if (!evento.podeSeInscrever(aluno)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aluno não atende aos critérios deste evento");
        }

        Inscricao inscricao = new Inscricao();
        inscricao.setAluno(aluno);
        inscricao.setEvento(evento);
        inscricao.setDataInscricao(new Date());
        inscricao.setPresenca(null);

        evento.getInscricoes().add(inscricao);
        return inscricaoRepository.save(inscricao);
    }

    public Inscricao atualizarPresenca(Integer inscricaoId, Boolean presente) {
        Inscricao inscricao = inscricaoRepository.findById(inscricaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscrição não encontrada"));
        inscricao.setPresenca(presente);
        return inscricaoRepository.save(inscricao);
    }

    public void remover(Integer inscricaoId) {
        inscricaoRepository.deleteById(inscricaoId);
    }
}