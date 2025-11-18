package com.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.model.domain.Certificado;

@Repository
public interface CertificadoRepository extends JpaRepository<Certificado, Integer> {

    Optional<Certificado> findByHashCertificado(String hashCertificado);

    List<Certificado> findByAlunoId(Integer alunoId);

    List<Certificado> findByEventoId(Integer eventoId);
}