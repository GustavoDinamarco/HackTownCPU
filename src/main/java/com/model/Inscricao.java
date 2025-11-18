package com.model;

import java.util.Date;

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
public class Inscricao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Aluno aluno;
    private Evento evento;
    private Date dataInscricao;
    private Boolean presenca; // true = presente, false = ausente, null = não confirmado
    
    // Construtor sem presença (para quando ainda não foi confirmada)
    public Inscricao(Aluno aluno, Evento evento, Date dataInscricao) {
        this.aluno = aluno;
        this.evento = evento;
        this.dataInscricao = dataInscricao;
        this.presenca = null; // Ainda não confirmado
    }
}