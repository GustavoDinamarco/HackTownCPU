package com.model.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.model.domain.Aluno;
import com.model.domain.Curso;
import com.model.domain.Evento;
import com.model.domain.Inscricao;
import com.model.repository.AlunoRepository;
import com.model.repository.CursoRepository;
import com.model.repository.EventoRepository;
import com.model.repository.InscricaoRepository;
import com.model.services.InscricaoService;

import jakarta.persistence.EntityManager;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - InscricaoService com Banco de Dados")
class InscricaoServiceIntegrationTest {

    @Autowired
    private InscricaoService inscricaoService;

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private EntityManager entityManager;

    private Aluno aluno;
    private Evento evento;
    private Curso curso;

    @BeforeEach
    void setUp() {
        inscricaoRepository.deleteAll();
        alunoRepository.deleteAll();
        eventoRepository.deleteAll();
        cursoRepository.deleteAll();
        
        // Cria curso
        curso = new Curso();
        curso.setNome("Ciência da Computação");
        curso = cursoRepository.save(curso);
        
        // Cria aluno
        aluno = new Aluno();
        aluno.setNome("João Silva");
        aluno.setCpf("123.456.789-00");
        aluno.setEmail("joao@email.com");
        aluno.setPeriodo("4º Período");
        aluno.setCursos(new ArrayList<>());
        aluno.getCursos().add(curso);
        aluno = alunoRepository.save(aluno);
        
        // Cria evento
        evento = new Evento();
        evento.setNome("Workshop Spring Boot");
        evento.setCursos(new ArrayList<>());
        evento.getCursos().add(curso);
        evento.setInscricoes(new ArrayList<>());
        evento.setVagas(50);
        evento = eventoRepository.save(evento);
    }

    @Test
    @DisplayName("Deve registrar inscrição no banco de dados")
    void deveRegistrarInscricaoNoBancoDeDados() {
        // Act
        Inscricao resultado = inscricaoService.registrar(evento.getId(), aluno.getId());

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals(aluno.getId(), resultado.getAluno().getId());
        assertEquals(evento.getId(), resultado.getEvento().getId());
        assertNull(resultado.getPresenca()); // Presença ainda não confirmada
        
        // Verifica que foi salvo no banco
        assertTrue(inscricaoRepository.existsById(resultado.getId()));
    }

    @Test
    @DisplayName("Deve listar inscrições por evento do banco de dados")
    void deveListarInscricoesPorEventoDoBancoDeDados() {
        // Arrange
        inscricaoService.registrar(evento.getId(), aluno.getId());
        
        Aluno aluno2 = new Aluno();
        aluno2.setNome("Maria Santos");
        aluno2.setCpf("987.654.321-00");
        aluno2.setEmail("maria@email.com");
        aluno2.setCursos(new ArrayList<>());
        aluno2.getCursos().add(curso);
        aluno2 = alunoRepository.save(aluno2);
        inscricaoService.registrar(evento.getId(), aluno2.getId());

        // Act
        List<Inscricao> resultado = inscricaoService.listarPorEvento(evento.getId());

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Deve listar inscrições por aluno do banco de dados")
    void deveListarInscricoesPorAlunoDoBancoDeDados() {
        // Arrange
        inscricaoService.registrar(evento.getId(), aluno.getId());
        
        Evento evento2 = new Evento();
        evento2.setNome("Palestra Java");
        evento2.setVagas(30);
        evento2.setCursos(new ArrayList<>());
        evento2.getCursos().add(curso);
        evento2.setInscricoes(new ArrayList<>());
        evento2 = eventoRepository.save(evento2);
        inscricaoService.registrar(evento2.getId(), aluno.getId());

        // Act
        List<Inscricao> resultado = inscricaoService.listarPorAluno(aluno.getId());

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Deve atualizar presença no banco de dados")
    void deveAtualizarPresencaNoBancoDeDados() {
        // Arrange
        Inscricao inscricao = inscricaoService.registrar(evento.getId(), aluno.getId());
        Integer inscricaoId = inscricao.getId();

        // Act
        Inscricao resultado = inscricaoService.atualizarPresenca(inscricaoId, true);

        // Assert
        assertNotNull(resultado);
        assertEquals(true, resultado.getPresenca());
        
        // Verifica no banco
        Inscricao doBanco = inscricaoRepository.findById(inscricaoId).orElse(null);
        assertNotNull(doBanco);
        assertEquals(true, doBanco.getPresenca());
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar inscrição duplicada")
    void deveLancarExcecaoAoRegistrarInscricaoDuplicada() {
        // Arrange
        inscricaoService.registrar(evento.getId(), aluno.getId());

        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> inscricaoService.registrar(evento.getId(), aluno.getId())
        );
        assertEquals("Aluno já inscrito neste evento", exception.getReason());
    }

    @Test
    @DisplayName("Deve remover inscrição do banco de dados")
    void deveRemoverInscricaoDoBancoDeDados() {
        // Arrange
        Inscricao inscricao = inscricaoService.registrar(evento.getId(), aluno.getId());
        Integer inscricaoId = inscricao.getId();
        Integer eventoId = evento.getId();
        
        // Garante que a inscrição foi salva
        assertTrue(inscricaoRepository.existsById(inscricaoId));
        long countAntes = inscricaoRepository.findByEventoId(eventoId).size();
        assertEquals(1, countAntes);

        // Act
        inscricaoService.remover(inscricaoId);
        
        // Força a sincronização com o banco e limpa o cache do Hibernate
        entityManager.flush();
        entityManager.clear();

        // Assert - verifica diretamente no banco através do repository
        // Verifica que o count diminuiu (de 1 para 0)
        long countDepois = inscricaoRepository.findByEventoId(eventoId).size();
        assertEquals(0, countDepois);
    }
}

