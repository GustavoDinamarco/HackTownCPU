package com.model.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.model.domain.Curso;
import com.model.repository.CursoRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CursoService")
class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private CursoService cursoService;

    private Curso curso;
    private Curso cursoAtualizado;

    @BeforeEach
    void setUp() {
        curso = new Curso();
        curso.setId(1);
        curso.setNome("Ciência da Computação");

        cursoAtualizado = new Curso();
        cursoAtualizado.setNome("Engenharia de Software");
    }

    @Test
    @DisplayName("Deve listar todos os cursos")
    void deveListarTodosOsCursos() {
        // Arrange
        List<Curso> cursos = Arrays.asList(curso);
        when(cursoRepository.findAll()).thenReturn(cursos);

        // Act
        List<Curso> resultado = cursoService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Ciência da Computação", resultado.get(0).getNome());
        verify(cursoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar curso por ID com sucesso")
    void deveBuscarCursoPorIdComSucesso() {
        // Arrange
        when(cursoRepository.findById(1)).thenReturn(Optional.of(curso));

        // Act
        Curso resultado = cursoService.buscarPorId(1);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Ciência da Computação", resultado.getNome());
        verify(cursoRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar curso com ID null")
    void deveLancarExcecaoAoBuscarCursoComIdNull() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> cursoService.buscarPorId(null)
        );
        assertEquals("ID não pode ser nulo", exception.getReason());
        verify(cursoRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar curso inexistente")
    void deveLancarExcecaoAoBuscarCursoInexistente() {
        // Arrange
        when(cursoRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> cursoService.buscarPorId(999)
        );
        assertEquals("Curso não encontrado", exception.getReason());
        verify(cursoRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("Deve criar curso com sucesso")
    void deveCriarCursoComSucesso() {
        // Arrange
        when(cursoRepository.existsByNomeIgnoreCase("Ciência da Computação")).thenReturn(false);
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        // Act
        Curso resultado = cursoService.criar(curso);

        // Assert
        assertNotNull(resultado);
        assertEquals("Ciência da Computação", resultado.getNome());
        verify(cursoRepository, times(1)).existsByNomeIgnoreCase("Ciência da Computação");
        verify(cursoRepository, times(1)).save(curso);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar curso com nome duplicado")
    void deveLancarExcecaoAoCriarCursoComNomeDuplicado() {
        // Arrange
        when(cursoRepository.existsByNomeIgnoreCase("Ciência da Computação")).thenReturn(true);

        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> cursoService.criar(curso)
        );
        assertEquals("Curso já cadastrado", exception.getReason());
        verify(cursoRepository, times(1)).existsByNomeIgnoreCase("Ciência da Computação");
        verify(cursoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar curso com sucesso")
    void deveAtualizarCursoComSucesso() {
        // Arrange
        when(cursoRepository.findById(1)).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        // Act
        Curso resultado = cursoService.atualizar(1, cursoAtualizado);

        // Assert
        assertNotNull(resultado);
        verify(cursoRepository, times(1)).findById(1);
        verify(cursoRepository, times(1)).save(curso);
    }

    @Test
    @DisplayName("Deve remover curso com sucesso")
    void deveRemoverCursoComSucesso() {
        // Arrange
        when(cursoRepository.findById(1)).thenReturn(Optional.of(curso));
        doNothing().when(cursoRepository).delete(any(Curso.class));

        // Act
        cursoService.remover(1);

        // Assert
        verify(cursoRepository, times(1)).findById(1);
        verify(cursoRepository, times(1)).delete(curso);
    }
}

