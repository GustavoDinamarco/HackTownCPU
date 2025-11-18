package com.model.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
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

import com.model.domain.Evento;
import com.model.repository.EventoRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para EventoService")
class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @InjectMocks
    private EventoService eventoService;

    private Evento evento;
    private Evento eventoAtualizado;

    @BeforeEach
    void setUp() {
        evento = new Evento();
        evento.setId(1);
        evento.setNome("Workshop Spring Boot");
        evento.setLocal("Auditório Principal");
        evento.setHoraInicio(LocalDateTime.now().plusDays(1));
        evento.setHoraFim(LocalDateTime.now().plusDays(1).plusHours(8));
        evento.setDescricao("Workshop completo sobre Spring Boot");
        evento.setCargaHoraria(8);
        evento.setVagas(50);

        eventoAtualizado = new Evento();
        eventoAtualizado.setNome("Workshop Spring Boot Avançado");
        eventoAtualizado.setDescricao("Workshop avançado");
        eventoAtualizado.setVagas(100);
    }

    @Test
    @DisplayName("Deve listar todos os eventos")
    void deveListarTodosOsEventos() {
        // Arrange
        List<Evento> eventos = Arrays.asList(evento);
        when(eventoRepository.findAll()).thenReturn(eventos);

        // Act
        List<Evento> resultado = eventoService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Workshop Spring Boot", resultado.get(0).getNome());
        verify(eventoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar evento por ID com sucesso")
    void deveBuscarEventoPorIdComSucesso() {
        // Arrange
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));

        // Act
        Evento resultado = eventoService.buscarPorId(1);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("Workshop Spring Boot", resultado.getNome());
        verify(eventoRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar evento com ID null")
    void deveLancarExcecaoAoBuscarEventoComIdNull() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> eventoService.buscarPorId(null)
        );
        assertEquals("ID não pode ser nulo", exception.getReason());
        verify(eventoRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve buscar eventos por categoria")
    void deveBuscarEventosPorCategoria() {
        // Arrange
        List<Evento> eventos = Arrays.asList(evento);
        when(eventoRepository.findByCategoriasContainingIgnoreCase("Workshop")).thenReturn(eventos);

        // Act
        List<Evento> resultado = eventoService.buscarPorCategoria("Workshop");

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(eventoRepository, times(1)).findByCategoriasContainingIgnoreCase("Workshop");
    }

    @Test
    @DisplayName("Deve listar todos os eventos quando categoria é null ou vazia")
    void deveListarTodosOsEventosQuandoCategoriaEhNullOuVazia() {
        // Arrange
        List<Evento> eventos = Arrays.asList(evento);
        when(eventoRepository.findAll()).thenReturn(eventos);

        // Act
        List<Evento> resultado1 = eventoService.buscarPorCategoria(null);
        List<Evento> resultado2 = eventoService.buscarPorCategoria("");

        // Assert
        assertNotNull(resultado1);
        assertNotNull(resultado2);
        verify(eventoRepository, times(2)).findAll();
    }

    @Test
    @DisplayName("Deve criar evento com sucesso")
    void deveCriarEventoComSucesso() {
        // Arrange
        when(eventoRepository.save(any(Evento.class))).thenReturn(evento);

        // Act
        Evento resultado = eventoService.criar(evento);

        // Assert
        assertNotNull(resultado);
        assertEquals("Workshop Spring Boot", resultado.getNome());
        verify(eventoRepository, times(1)).save(evento);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar evento null")
    void deveLancarExcecaoAoCriarEventoNull() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> eventoService.criar(null)
        );
        assertEquals("Evento não pode ser nulo", exception.getReason());
        verify(eventoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar evento com sucesso")
    void deveAtualizarEventoComSucesso() {
        // Arrange
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(eventoRepository.save(any(Evento.class))).thenReturn(evento);

        // Act
        Evento resultado = eventoService.atualizar(1, eventoAtualizado);

        // Assert
        assertNotNull(resultado);
        verify(eventoRepository, times(1)).findById(1);
        verify(eventoRepository, times(1)).save(evento);
    }

    @Test
    @DisplayName("Deve remover evento com sucesso")
    void deveRemoverEventoComSucesso() {
        // Arrange
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        doNothing().when(eventoRepository).delete(any(Evento.class));

        // Act
        eventoService.remover(1);

        // Assert
        verify(eventoRepository, times(1)).findById(1);
        verify(eventoRepository, times(1)).delete(evento);
    }
}
