package com.model.domain;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "inscricoes")
public class Inscricao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @Column(name = "data_inscricao")
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