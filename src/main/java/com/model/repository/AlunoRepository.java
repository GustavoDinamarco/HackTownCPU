package com.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.model.domain.Aluno;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Integer> {

    boolean existsByCpf(String cpf);

    Optional<Aluno> findByEmail(String email);
}