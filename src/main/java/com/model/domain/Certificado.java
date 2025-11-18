package com.model.domain;

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
@Table(name = "certificados")
public class Certificado {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne
    @JoinColumn(name = "palestrante_id", nullable = false)
    private Palestrante palestrante;
    private String hashCertificado;
    private String nomeInstituicao;
    private String identidadeInstituicao;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Certificado{");
        sb.append("nomeCompleto=").append(aluno.getNome());
        sb.append(", evento=").append(evento.getNome());
        sb.append(", palestrante=").append(palestrante.getNome());
        sb.append(", instituicao=").append(nomeInstituicao);
        sb.append('}');
        return sb.toString();
    }
}
