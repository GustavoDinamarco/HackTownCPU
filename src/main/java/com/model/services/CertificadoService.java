package com.model.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.model.domain.Aluno;
import com.model.domain.Certificado;
import com.model.domain.Evento;
import com.model.domain.Palestrante;
import com.model.repository.AlunoRepository;
import com.model.repository.CertificadoRepository;
import com.model.repository.EventoRepository;
import com.model.repository.PalestranteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificadoService {

    private final CertificadoRepository certificadoRepository;
    private final AlunoRepository alunoRepository;
    private final EventoRepository eventoRepository;
    private final PalestranteRepository palestranteRepository;

    public List<Certificado> listarTodos() {
        return certificadoRepository.findAll();
    }

    public List<Certificado> listarPorAluno(Integer alunoId) {
        return certificadoRepository.findByAlunoId(alunoId);
    }

    public List<Certificado> listarPorEvento(Integer eventoId) {
        return certificadoRepository.findByEventoId(eventoId);
    }

    public Certificado buscarPorHash(String hash) {
        return certificadoRepository.findByHashCertificado(hash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificado não encontrado"));
    }

    public Certificado emitir(Integer alunoId, Integer eventoId, Integer palestranteId, Certificado certificadoPayload) {
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));
        Palestrante palestrante = palestranteRepository.findById(palestranteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Palestrante não encontrado"));

        Certificado certificado = new Certificado();
        certificado.setAluno(aluno);
        certificado.setEvento(evento);
        certificado.setPalestrante(palestrante);
        certificado.setHashCertificado(certificadoPayload.getHashCertificado());
        certificado.setNomeInstituicao(certificadoPayload.getNomeInstituicao());
        certificado.setIdentidadeInstituicao(certificadoPayload.getIdentidadeInstituicao());
        return certificadoRepository.save(certificado);
    }

    public void remover(Integer id) {
        certificadoRepository.deleteById(id);
    }
}