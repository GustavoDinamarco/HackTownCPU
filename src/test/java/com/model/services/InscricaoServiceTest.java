package com.model.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
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
import com.model.domain.Curso;
import com.model.domain.Evento;
import com.model.domain.Inscricao;
import com.model.repository.AlunoRepository;
import com.model.repository.EventoRepository;
import com.model.repository.InscricaoRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para InscricaoService")
class InscricaoServiceTest {

    @Mock
    private InscricaoRepository inscricaoRepository;

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @InjectMocks
    private InscricaoService inscricaoService;

    private Aluno aluno;
    private Evento evento;
    private Inscricao inscricao;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setId(1);
        aluno.setNome("João Silva");
        aluno.setCursos(new ArrayList<>());

        Curso curso = new Curso();
        curso.setId(1);
        curso.setNome("Ciência da Computação");
        aluno.getCursos().add(curso);

        evento = new Evento();
        evento.setId(1);
        evento.setNome("Workshop Spring Boot");
        evento.setVagas(50);
        evento.setCursos(new ArrayList<>());
        evento.getCursos().add(curso);
        evento.setInscricoes(new ArrayList<>());

        inscricao = new Inscricao();
        inscricao.setId(1);
        inscricao.setAluno(aluno);
        inscricao.setEvento(evento);
        inscricao.setDataInscricao(new Date());
        inscricao.setPresenca(null);
    }

    @Test
    @DisplayName("Deve listar inscrições por evento")
    void deveListarInscricoesPorEvento() {
        // Arrange
        List<Inscricao> inscricoes = Arrays.asList(inscricao);
        when(inscricaoRepository.findByEventoId(1)).thenReturn(inscricoes);

        // Act
        List<Inscricao> resultado = inscricaoService.listarPorEvento(1);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(inscricaoRepository, times(1)).findByEventoId(1);
    }

    @Test
    @DisplayName("Deve listar inscrições por aluno")
    void deveListarInscricoesPorAluno() {
        // Arrange
        List<Inscricao> inscricoes = Arrays.asList(inscricao);
        when(inscricaoRepository.findByAlunoId(1)).thenReturn(inscricoes);

        // Act
        List<Inscricao> resultado = inscricaoService.listarPorAluno(1);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(inscricaoRepository, times(1)).findByAlunoId(1);
    }

    @Test
    @DisplayName("Deve registrar inscrição com sucesso")
    void deveRegistrarInscricaoComSucesso() {
        // Arrange
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));
        when(inscricaoRepository.existsByEventoIdAndAlunoId(1, 1)).thenReturn(false);
        when(inscricaoRepository.save(any(Inscricao.class))).thenReturn(inscricao);

        // Act
        Inscricao resultado = inscricaoService.registrar(1, 1);

        // Assert
        assertNotNull(resultado);
        verify(eventoRepository, times(1)).findById(1);
        verify(alunoRepository, times(1)).findById(1);
        verify(inscricaoRepository, times(1)).existsByEventoIdAndAlunoId(1, 1);
        verify(inscricaoRepository, times(1)).save(any(Inscricao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar inscrição com eventoId null")
    void deveLancarExcecaoAoRegistrarInscricaoComEventoIdNull() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> inscricaoService.registrar(null, 1)
        );
        assertEquals("ID do evento não pode ser nulo", exception.getReason());
        verify(inscricaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar inscrição com alunoId null")
    void deveLancarExcecaoAoRegistrarInscricaoComAlunoIdNull() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> inscricaoService.registrar(1, null)
        );
        assertEquals("ID do aluno não pode ser nulo", exception.getReason());
        verify(inscricaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar inscrição duplicada")
    void deveLancarExcecaoAoRegistrarInscricaoDuplicada() {
        // Arrange
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));
        when(inscricaoRepository.existsByEventoIdAndAlunoId(1, 1)).thenReturn(true);

        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> inscricaoService.registrar(1, 1)
        );
        assertEquals("Aluno já inscrito neste evento", exception.getReason());
        verify(inscricaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar presença com sucesso")
    void deveAtualizarPresencaComSucesso() {
        // Arrange
        when(inscricaoRepository.findById(1)).thenReturn(Optional.of(inscricao));
        when(inscricaoRepository.save(any(Inscricao.class))).thenReturn(inscricao);

        // Act
        Inscricao resultado = inscricaoService.atualizarPresenca(1, true);

        // Assert
        assertNotNull(resultado);
        assertEquals(true, resultado.getPresenca());
        verify(inscricaoRepository, times(1)).findById(1);
        verify(inscricaoRepository, times(1)).save(inscricao);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar presença com ID null")
    void deveLancarExcecaoAoAtualizarPresencaComIdNull() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> inscricaoService.atualizarPresenca(null, true)
        );
        assertEquals("ID da inscrição não pode ser nulo", exception.getReason());
        verify(inscricaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve remover inscrição com sucesso")
    void deveRemoverInscricaoComSucesso() {
        // Arrange
        doNothing().when(inscricaoRepository).deleteById(1);

        // Act
        inscricaoService.remover(1);

        // Assert
        verify(inscricaoRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover inscrição com ID null")
    void deveLancarExcecaoAoRemoverInscricaoComIdNull() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> inscricaoService.remover(null)
        );
        assertEquals("ID da inscrição não pode ser nulo", exception.getReason());
        verify(inscricaoRepository, never()).deleteById(any());
    }
}

