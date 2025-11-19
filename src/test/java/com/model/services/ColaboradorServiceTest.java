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

import com.model.domain.Colaborador;
import com.model.repository.ColaboradorRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para ColaboradorService")
class ColaboradorServiceTest {

    @Mock
    private ColaboradorRepository colaboradorRepository;

    @InjectMocks
    private ColaboradorService colaboradorService;

    private Colaborador colaborador;
    private Colaborador colaboradorAtualizado;

    @BeforeEach
    void setUp() {
        colaborador = new Colaborador();
        colaborador.setId(1);
        colaborador.setNome("Pedro Costa");
        colaborador.setCpf("111.222.333-44");
        colaborador.setEmail("pedro@email.com");
        colaborador.setContato("(11) 99876-5432");
        colaborador.setDataNascimento(new Date());
        colaborador.setCargo("Coordenador de Eventos");

        colaboradorAtualizado = new Colaborador();
        colaboradorAtualizado.setNome("Pedro Costa");
        colaboradorAtualizado.setCargo("Gerente de Eventos");
    }

    @Test
    @DisplayName("Deve listar todos os colaboradores")
    void deveListarTodosOsColaboradores() {
        // Arrange
        List<Colaborador> colaboradores = Arrays.asList(colaborador);
        when(colaboradorRepository.findAll()).thenReturn(colaboradores);

        // Act
        List<Colaborador> resultado = colaboradorService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Pedro Costa", resultado.get(0).getNome());
        verify(colaboradorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar colaborador por ID com sucesso")
    void deveBuscarColaboradorPorIdComSucesso() {
        // Arrange
        when(colaboradorRepository.findById(1)).thenReturn(Optional.of(colaborador));

        // Act
        Colaborador resultado = colaboradorService.buscarPorId(1);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Pedro Costa", resultado.getNome());
        verify(colaboradorRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar colaborador com ID null")
    void deveLancarExcecaoAoBuscarColaboradorComIdNull() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> colaboradorService.buscarPorId(null)
        );
        assertEquals("ID não pode ser nulo", exception.getReason());
        verify(colaboradorRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve criar colaborador com sucesso")
    void deveCriarColaboradorComSucesso() {
        // Arrange
        when(colaboradorRepository.save(any(Colaborador.class))).thenReturn(colaborador);

        // Act
        Colaborador resultado = colaboradorService.criar(colaborador);

        // Assert
        assertNotNull(resultado);
        assertEquals("Pedro Costa", resultado.getNome());
        verify(colaboradorRepository, times(1)).save(colaborador);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar colaborador null")
    void deveLancarExcecaoAoCriarColaboradorNull() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> colaboradorService.criar(null)
        );
        assertEquals("Colaborador não pode ser nulo", exception.getReason());
        verify(colaboradorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar colaborador com sucesso")
    void deveAtualizarColaboradorComSucesso() {
        // Arrange
        when(colaboradorRepository.findById(1)).thenReturn(Optional.of(colaborador));
        when(colaboradorRepository.save(any(Colaborador.class))).thenReturn(colaborador);

        // Act
        Colaborador resultado = colaboradorService.atualizar(1, colaboradorAtualizado);

        // Assert
        assertNotNull(resultado);
        verify(colaboradorRepository, times(1)).findById(1);
        verify(colaboradorRepository, times(1)).save(colaborador);
    }

    @Test
    @DisplayName("Deve remover colaborador com sucesso")
    void deveRemoverColaboradorComSucesso() {
        // Arrange
        when(colaboradorRepository.findById(1)).thenReturn(Optional.of(colaborador));
        doNothing().when(colaboradorRepository).delete(any(Colaborador.class));

        // Act
        colaboradorService.remover(1);

        // Assert
        verify(colaboradorRepository, times(1)).findById(1);
        verify(colaboradorRepository, times(1)).delete(colaborador);
    }
}

