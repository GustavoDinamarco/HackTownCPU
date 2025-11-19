package com.model.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Date;
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

import com.model.domain.Aluno;
import com.model.repository.AlunoRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para AlunoService")
class AlunoServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @InjectMocks
    private AlunoService alunoService;

    private Aluno aluno;
    private Aluno alunoAtualizado;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setId(1);
        aluno.setNome("João Silva");
        aluno.setCpf("123.456.789-00");
        aluno.setEmail("joao@email.com");
        aluno.setContato("(11) 98765-4321");
        aluno.setDataNascimento(new Date());
        aluno.setPeriodo("4º Período");

        alunoAtualizado = new Aluno();
        alunoAtualizado.setNome("João Silva Santos");
        alunoAtualizado.setCpf("123.456.789-00");
        alunoAtualizado.setEmail("joao.santos@email.com");
        alunoAtualizado.setPeriodo("5º Período");
    }

    @Test
    @DisplayName("Deve listar todos os alunos")
    void deveListarTodosOsAlunos() {
        // Arrange
        List<Aluno> alunos = Arrays.asList(aluno);
        when(alunoRepository.findAll()).thenReturn(alunos);

        // Act
        List<Aluno> resultado = alunoService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("João Silva", resultado.get(0).getNome());
        verify(alunoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar aluno por ID com sucesso")
    void deveBuscarAlunoPorIdComSucesso() {
        // Arrange
        when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));

        // Act
        Aluno resultado = alunoService.buscarPorId(1);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("João Silva", resultado.getNome());
        verify(alunoRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar aluno com ID null")
    void deveLancarExcecaoAoBuscarAlunoComIdNull() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> alunoService.buscarPorId(null)
        );
        assertEquals("ID não pode ser nulo", exception.getReason());
        verify(alunoRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar aluno inexistente")
    void deveLancarExcecaoAoBuscarAlunoInexistente() {
        // Arrange
        when(alunoRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> alunoService.buscarPorId(999)
        );
        assertEquals("Aluno não encontrado", exception.getReason());
        verify(alunoRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("Deve criar aluno com sucesso")
    void deveCriarAlunoComSucesso() {
        // Arrange
        when(alunoRepository.existsByCpf("123.456.789-00")).thenReturn(false);
        when(alunoRepository.save(any(Aluno.class))).thenReturn(aluno);

        // Act
        Aluno resultado = alunoService.criar(aluno);

        // Assert
        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        verify(alunoRepository, times(1)).existsByCpf("123.456.789-00");
        verify(alunoRepository, times(1)).save(aluno);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar aluno com CPF duplicado")
    void deveLancarExcecaoAoCriarAlunoComCpfDuplicado() {
        // Arrange
        when(alunoRepository.existsByCpf("123.456.789-00")).thenReturn(true);

        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> alunoService.criar(aluno)
        );
        assertEquals("CPF já cadastrado", exception.getReason());
        verify(alunoRepository, times(1)).existsByCpf("123.456.789-00");
        verify(alunoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar aluno com sucesso")
    void deveAtualizarAlunoComSucesso() {
        // Arrange
        when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));
        when(alunoRepository.save(any(Aluno.class))).thenReturn(aluno);

        // Act
        Aluno resultado = alunoService.atualizar(1, alunoAtualizado);

        // Assert
        assertNotNull(resultado);
        verify(alunoRepository, times(1)).findById(1);
        verify(alunoRepository, times(1)).save(aluno);
    }

    @Test
    @DisplayName("Deve remover aluno com sucesso")
    void deveRemoverAlunoComSucesso() {
        // Arrange
        when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));
        doNothing().when(alunoRepository).delete(any(Aluno.class));

        // Act
        alunoService.remover(1);

        // Assert
        verify(alunoRepository, times(1)).findById(1);
        verify(alunoRepository, times(1)).delete(aluno);
    }
}

