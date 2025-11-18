package com.model.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.model.domain.Evento;
import com.model.repository.EventoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EventoService {

    private final EventoRepository eventoRepository;

    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }

    public Evento buscarPorId(Integer id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));
    }

    public List<Evento> buscarPorCategoria(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            return listarTodos();
        }
        return eventoRepository.findByCategoriasContainingIgnoreCase(categoria);
    }

    public Evento criar(Evento evento) {
        return eventoRepository.save(evento);
    }

    public Evento atualizar(Integer id, Evento eventoAtualizado) {
        Evento existente = buscarPorId(id);
        existente.setNome(eventoAtualizado.getNome());
        existente.setDescricao(eventoAtualizado.getDescricao());
        existente.setLocal(eventoAtualizado.getLocal());
        existente.setHoraInicio(eventoAtualizado.getHoraInicio());
        existente.setHoraFim(eventoAtualizado.getHoraFim());
        existente.setCategorias(eventoAtualizado.getCategorias());
        existente.setCursos(eventoAtualizado.getCursos());
        existente.setCargaHoraria(eventoAtualizado.getCargaHoraria());
        existente.setVagas(eventoAtualizado.getVagas());
        existente.setBannerUrl(eventoAtualizado.getBannerUrl());
        existente.setPalestrantes(eventoAtualizado.getPalestrantes());
        return eventoRepository.save(existente);
    }

    public void remover(Integer id) {
        eventoRepository.delete(buscarPorId(id));
    }
}