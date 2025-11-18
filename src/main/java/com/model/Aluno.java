package com.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Aluno extends Pessoa{

    private List<Curso> cursos;
    private List<Certificado> certificados = new ArrayList<>();
    private String periodo;
    private List<Inscricao> inscricoes = new ArrayList<>();
}
