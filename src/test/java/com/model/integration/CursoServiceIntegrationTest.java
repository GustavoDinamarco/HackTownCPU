package com.model.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.model.domain.Curso;
import com.model.repository.CursoRepository;
import com.model.services.CursoService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - CursoService com Banco de Dados")
class CursoServiceIntegrationTest {

    @Autowired
    private CursoService cursoService;

    @Autowired
    private CursoRepository cursoRepository;

    private Curso curso;

    @BeforeEach
    void setUp() {
        cursoRepository.deleteAll();
        
        curso = new Curso();
        curso.setNome("Ciência da Computação");
    }

    @Test
    @DisplayName("Deve criar e salvar curso no banco de dados")
    void deveCriarESalvarCursoNoBancoDeDados() {
        // Act
        Curso resultado = cursoService.criar(curso);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals("Ciência da Computação", resultado.getNome());
        
        // Verifica que foi salvo no banco
        Curso salvo = cursoRepository.findById(resultado.getId()).orElse(null);
        assertNotNull(salvo);
        assertEquals("Ciência da Computação", salvo.getNome());
    }

    @Test
    @DisplayName("Deve listar todos os cursos do banco de dados")
    void deveListarTodosOsCursosDoBancoDeDados() {
        // Arrange
        cursoService.criar(curso);
        
        Curso curso2 = new Curso();
        curso2.setNome("Engenharia de Software");
        cursoService.criar(curso2);

        // Act
        List<Curso> resultado = cursoService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Deve buscar curso do banco de dados por ID")
    void deveBuscarCursoDoBancoDeDadosPorId() {
        // Arrange
        Curso criado = cursoService.criar(curso);
        Integer id = criado.getId();

        // Act
        Curso resultado = cursoService.buscarPorId(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Ciência da Computação", resultado.getNome());
    }

    @Test
    @DisplayName("Deve atualizar curso no banco de dados")
    void deveAtualizarCursoNoBancoDeDados() {
        // Arrange
        Curso criado = cursoService.criar(curso);
        Integer id = criado.getId();
        
        Curso atualizado = new Curso();
        atualizado.setNome("Engenharia de Software");

        // Act
        Curso resultado = cursoService.atualizar(id, atualizado);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Engenharia de Software", resultado.getNome());
        
        // Verifica no banco
        Curso doBanco = cursoRepository.findById(id).orElse(null);
        assertNotNull(doBanco);
        assertEquals("Engenharia de Software", doBanco.getNome());
    }

    @Test
    @DisplayName("Deve remover curso do banco de dados")
    void deveRemoverCursoDoBancoDeDados() {
        // Arrange
        Curso criado = cursoService.criar(curso);
        Integer id = criado.getId();

        // Act
        cursoService.remover(id);

        // Assert
        assertFalse(cursoRepository.existsById(id));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar curso com nome duplicado")
    void deveLancarExcecaoAoCriarCursoComNomeDuplicado() {
        // Arrange
        cursoService.criar(curso);
        
        Curso cursoDuplicado = new Curso();
        cursoDuplicado.setNome("Ciência da Computação"); // Mesmo nome (case insensitive)

        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> cursoService.criar(cursoDuplicado)
        );
        assertEquals("Curso já cadastrado", exception.getReason());
        
        // Verifica que só existe um curso no banco
        assertEquals(1, cursoRepository.count());
    }
}

