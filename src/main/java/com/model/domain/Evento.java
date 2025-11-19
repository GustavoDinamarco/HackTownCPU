package com.model.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;

    @ManyToMany
    @JoinTable(name = "evento_curso",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "curso_id"))
    private List<Curso> cursos = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "evento_palestrante",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "palestrante_id"))
    private List<Palestrante> palestrantes = new ArrayList<>();

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inscricao> inscricoes = new ArrayList<>();

    private String local;
    
    @Column(name = "hora_inicio")
    private LocalDateTime horaInicio;
    
    @Column(name = "hora_fim")
    private LocalDateTime horaFim;
    private String descricao;

    @ElementCollection
    @CollectionTable(name = "evento_categorias", joinColumns = @JoinColumn(name = "evento_id"))
    @Column(name = "categoria")
    private List<String> categorias = new ArrayList<>();
    
    @Column(name = "carga_horaria")
    private Integer cargaHoraria;
    private Integer vagas;
    
    @Column(name = "banner_url")
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
