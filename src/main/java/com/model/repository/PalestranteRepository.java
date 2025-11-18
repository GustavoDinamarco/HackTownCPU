package com.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.model.domain.Palestrante;

@Repository
public interface PalestranteRepository extends JpaRepository<Palestrante, Integer> {

    boolean existsByEmail(String email);
}