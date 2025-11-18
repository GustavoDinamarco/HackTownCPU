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

    public boolean isAbertoATodos() {
        return cursos == null || cursos.isEmpty();
    }

    public boolean podeSeInscrever(Aluno aluno) {
        // Se o evento está aberto a todos, qualquer aluno pode se inscrever
        if (isAbertoATodos()) {
            return true;
        }
        
        // Se o aluno não tem cursos cadastrados, não pode se inscrever em evento restrito
        if (aluno.getCursos() == null || aluno.getCursos().isEmpty()) {
            return false;
        }
        
        // Verifica se o aluno tem pelo menos um curso permitido
        return aluno.getCursos().stream()
            .anyMatch(cursoAluno -> cursos.stream()
                .anyMatch(cursoEvento -> cursoEvento.getId().equals(cursoAluno.getId())));
    }

    public void adicionarCurso(Curso curso) {
        if (curso != null && !cursos.contains(curso)) {
            cursos.add(curso);
        }
    }

    public void removerCurso(Curso curso) {
        if (curso != null) {
            cursos.remove(curso);
        }
    }

    public void tornarAbertoATodos() {
        cursos.clear();
    }

    public boolean isRestrito() {
        return !isAbertoATodos();
    }

    public int getVagasDisponiveis() {
        if (vagas == null) {
            return 0;
        }
        
        long inscritos = inscricoes != null ? inscricoes.size() : 0;
        return Math.max(0, vagas - (int) inscritos);
    }

    public boolean temVagasDisponiveis() {
        return getVagasDisponiveis() > 0;
    }
}
