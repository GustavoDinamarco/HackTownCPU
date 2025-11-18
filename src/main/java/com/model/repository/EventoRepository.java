package com.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.model.domain.Evento;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Integer> {

    List<Evento> findByCategoriasContainingIgnoreCase(String categoria);

    List<Evento> findByNomeContainingIgnoreCase(String nome);
}