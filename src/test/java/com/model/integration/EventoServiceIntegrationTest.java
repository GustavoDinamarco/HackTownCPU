package com.model.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.model.domain.Evento;
import com.model.repository.EventoRepository;
import com.model.services.EventoService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - EventoService com Banco de Dados")
class EventoServiceIntegrationTest {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private EventoRepository eventoRepository;

    private Evento evento;

    @BeforeEach
    void setUp() {
        eventoRepository.deleteAll();
        
        evento = new Evento();
        evento.setNome("Workshop Spring Boot");
        evento.setLocal("Auditório Principal");
        evento.setHoraInicio(LocalDateTime.now().plusDays(1));
        evento.setHoraFim(LocalDateTime.now().plusDays(1).plusHours(8));
        evento.setDescricao("Workshop completo sobre Spring Boot");
        evento.setCategorias(Arrays.asList("Workshop", "Tecnologia"));
        evento.setCargaHoraria(8);
        evento.setVagas(50);
    }

    @Test
    @DisplayName("Deve criar e salvar evento no banco de dados")
    void deveCriarESalvarEventoNoBancoDeDados() {
        // Act
        Evento resultado = eventoService.criar(evento);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals("Workshop Spring Boot", resultado.getNome());
        assertEquals(8, resultado.getCargaHoraria());
        assertEquals(50, resultado.getVagas());
        
        // Verifica que foi salvo no banco
        Evento salvo = eventoRepository.findById(resultado.getId()).orElse(null);
        assertNotNull(salvo);
        assertEquals("Workshop Spring Boot", salvo.getNome());
    }

    @Test
    @DisplayName("Deve buscar evento do banco de dados por ID")
    void deveBuscarEventoDoBancoDeDadosPorId() {
        // Arrange
        Evento criado = eventoService.criar(evento);
        Integer id = criado.getId();

        // Act
        Evento resultado = eventoService.buscarPorId(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Workshop Spring Boot", resultado.getNome());
    }

    @Test
    @DisplayName("Deve buscar eventos por categoria no banco de dados")
    void deveBuscarEventosPorCategoriaNoBancoDeDados() {
        // Arrange
        eventoService.criar(evento);
        
        Evento evento2 = new Evento();
        evento2.setNome("Palestra Java");
        evento2.setCategorias(Arrays.asList("Palestra", "Tecnologia"));
        eventoService.criar(evento2);

        // Act
        List<Evento> resultado = eventoService.buscarPorCategoria("Workshop");

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Workshop Spring Boot", resultado.get(0).getNome());
    }

    @Test
    @DisplayName("Deve atualizar evento no banco de dados")
    void deveAtualizarEventoNoBancoDeDados() {
        // Arrange
        Evento criado = eventoService.criar(evento);
        Integer id = criado.getId();
        
        Evento atualizado = new Evento();
        atualizado.setNome("Workshop Spring Boot Avançado");
        atualizado.setDescricao("Workshop avançado");
        atualizado.setVagas(100);
        atualizado.setCategorias(new ArrayList<>(Arrays.asList("Workshop", "Avançado")));

        // Act
        Evento resultado = eventoService.atualizar(id, atualizado);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Workshop Spring Boot Avançado", resultado.getNome());
        assertEquals(100, resultado.getVagas());
        
        // Verifica no banco
        Evento doBanco = eventoRepository.findById(id).orElse(null);
        assertNotNull(doBanco);
        assertEquals("Workshop Spring Boot Avançado", doBanco.getNome());
    }

    @Test
    @DisplayName("Deve remover evento do banco de dados")
    void deveRemoverEventoDoBancoDeDados() {
        // Arrange
        Evento criado = eventoService.criar(evento);
        Integer id = criado.getId();

        // Act
        eventoService.remover(id);

        // Assert
        assertFalse(eventoRepository.existsById(id));
    }
}

