package com.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.model.domain.Inscricao;

@Repository
public interface InscricaoRepository extends JpaRepository<Inscricao, Integer> {

    List<Inscricao> findByEventoId(Integer eventoId);

    List<Inscricao> findByAlunoId(Integer alunoId);

    boolean existsByEventoIdAndAlunoId(Integer eventoId, Integer alunoId);
}