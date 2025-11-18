package com.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String nome;
    private List<Curso> cursos = new ArrayList<>();
    private List<Palestrante> palestrantes = new ArrayList<>();
    private List<Inscricao> inscricoes = new ArrayList<>();
    private String local;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFim;
    private String descricao;
    private List<String> categorias = new ArrayList<>();
    private Integer cargaHoraria;
    private Integer vagas;
    private String bannerUrl;

}
