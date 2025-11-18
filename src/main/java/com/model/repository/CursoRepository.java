package com.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.model.domain.Curso;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Integer> {

    boolean existsByNomeIgnoreCase(String nome);
}