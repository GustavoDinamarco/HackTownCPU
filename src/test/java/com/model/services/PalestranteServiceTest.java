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

import com.model.domain.Palestrante;
import com.model.repository.PalestranteRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para PalestranteService")
class PalestranteServiceTest {

    @Mock
    private PalestranteRepository palestranteRepository;

    @InjectMocks
    private PalestranteService palestranteService;

    private Palestrante palestrante;
    private Palestrante palestranteAtualizado;

    @BeforeEach
    void setUp() {
        palestrante = new Palestrante();
        palestrante.setId(1);
        palestrante.setNome("Maria Santos");
        palestrante.setEmail("maria@email.com");
        palestrante.setCpf("987.654.321-00");
        palestrante.setContato("(11) 91234-5678");
        palestrante.setDataNascimento(new Date());
        palestrante.setDescricao("Especialista em Java");
        palestrante.setFotoUrl("https://example.com/foto.jpg");

        palestranteAtualizado = new Palestrante();
        palestranteAtualizado.setNome("Maria Santos Oliveira");
        palestranteAtualizado.setEmail("maria.oliveira@email.com");
        palestranteAtualizado.setDescricao("Especialista em Java e Spring Boot");
    }

    @Test
    @DisplayName("Deve listar todos os palestrantes")
    void deveListarTodosOsPalestrantes() {
        // Arrange
        List<Palestrante> palestrantes = Arrays.asList(palestrante);
        when(palestranteRepository.findAll()).thenReturn(palestrantes);

        // Act
        List<Palestrante> resultado = palestranteService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Maria Santos", resultado.get(0).getNome());
        verify(palestranteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar palestrante por ID com sucesso")
    void deveBuscarPalestrantePorIdComSucesso() {
        // Arrange
        when(palestranteRepository.findById(1)).thenReturn(Optional.of(palestrante));

        // Act
        Palestrante resultado = palestranteService.buscarPorId(1);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Maria Santos", resultado.getNome());
        verify(palestranteRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar palestrante com ID null")
    void deveLancarExcecaoAoBuscarPalestranteComIdNull() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> palestranteService.buscarPorId(null)
        );
        assertEquals("ID não pode ser nulo", exception.getReason());
        verify(palestranteRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve criar palestrante com sucesso")
    void deveCriarPalestranteComSucesso() {
        // Arrange
        when(palestranteRepository.existsByEmail("maria@email.com")).thenReturn(false);
        when(palestranteRepository.save(any(Palestrante.class))).thenReturn(palestrante);

        // Act
        Palestrante resultado = palestranteService.criar(palestrante);

        // Assert
        assertNotNull(resultado);
        assertEquals("Maria Santos", resultado.getNome());
        verify(palestranteRepository, times(1)).existsByEmail("maria@email.com");
        verify(palestranteRepository, times(1)).save(palestrante);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar palestrante com email duplicado")
    void deveLancarExcecaoAoCriarPalestranteComEmailDuplicado() {
        // Arrange
        when(palestranteRepository.existsByEmail("maria@email.com")).thenReturn(true);

        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> palestranteService.criar(palestrante)
        );
        assertEquals("E-mail já cadastrado", exception.getReason());
        verify(palestranteRepository, times(1)).existsByEmail("maria@email.com");
        verify(palestranteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar palestrante com sucesso")
    void deveAtualizarPalestranteComSucesso() {
        // Arrange
        when(palestranteRepository.findById(1)).thenReturn(Optional.of(palestrante));
        when(palestranteRepository.save(any(Palestrante.class))).thenReturn(palestrante);

        // Act
        Palestrante resultado = palestranteService.atualizar(1, palestranteAtualizado);

        // Assert
        assertNotNull(resultado);
        verify(palestranteRepository, times(1)).findById(1);
        verify(palestranteRepository, times(1)).save(palestrante);
    }

    @Test
    @DisplayName("Deve remover palestrante com sucesso")
    void deveRemoverPalestranteComSucesso() {
        // Arrange
        when(palestranteRepository.findById(1)).thenReturn(Optional.of(palestrante));
        doNothing().when(palestranteRepository).delete(any(Palestrante.class));

        // Act
        palestranteService.remover(1);

        // Assert
        verify(palestranteRepository, times(1)).findById(1);
        verify(palestranteRepository, times(1)).delete(palestrante);
    }
}

