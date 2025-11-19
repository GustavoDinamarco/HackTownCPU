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

import com.model.domain.Aluno;
import com.model.domain.Certificado;
import com.model.domain.Evento;
import com.model.domain.Inscricao;
import com.model.domain.Palestrante;
import com.model.repository.AlunoRepository;
import com.model.repository.CertificadoRepository;
import com.model.repository.EventoRepository;
import com.model.repository.InscricaoRepository;
import com.model.repository.PalestranteRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CertificadoService")
class CertificadoServiceTest {

    @Mock
    private CertificadoRepository certificadoRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private PalestranteRepository palestranteRepository;

    @Mock
    private InscricaoRepository inscricaoRepository;

    @InjectMocks
    private CertificadoService certificadoService;

    private Aluno aluno;
    private Evento evento;
    private Palestrante palestrante;
    private Certificado certificado;
    private Certificado certificadoPayload;
    private Inscricao inscricao;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setId(1);
        aluno.setNome("João Silva");
        aluno.setCpf("123.456.789-00");

        evento = new Evento();
        evento.setId(1);
        evento.setNome("Workshop Spring Boot");

        palestrante = new Palestrante();
        palestrante.setId(1);
        palestrante.setNome("Maria Santos");

        certificado = new Certificado();
        certificado.setId(1);
        certificado.setAluno(aluno);
        certificado.setEvento(evento);
        certificado.setPalestrante(palestrante);
        certificado.setHashCertificado("ABC123");
        certificado.setNomeInstituicao("UNINCOR");
        certificado.setIdentidadeInstituicao("12345678901234");

        certificadoPayload = new Certificado();
        certificadoPayload.setHashCertificado("ABC123");
        certificadoPayload.setNomeInstituicao("UNINCOR");
        certificadoPayload.setIdentidadeInstituicao("12345678901234");

        inscricao = new Inscricao();
        inscricao.setId(1);
        inscricao.setAluno(aluno);
        inscricao.setEvento(evento);
        inscricao.setPresenca(true);
    }

    @Test
    @DisplayName("Deve listar todos os certificados")
    void deveListarTodosOsCertificados() {
        // Arrange
        List<Certificado> certificados = Arrays.asList(certificado);
        when(certificadoRepository.findAll()).thenReturn(certificados);

        // Act
        List<Certificado> resultado = certificadoService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(certificadoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve listar certificados por aluno")
    void deveListarCertificadosPorAluno() {
        // Arrange
        List<Certificado> certificados = Arrays.asList(certificado);
        when(certificadoRepository.findByAlunoId(1)).thenReturn(certificados);

        // Act
        List<Certificado> resultado = certificadoService.listarPorAluno(1);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(certificadoRepository, times(1)).findByAlunoId(1);
    }

    @Test
    @DisplayName("Deve listar certificados por evento")
    void deveListarCertificadosPorEvento() {
        // Arrange
        List<Certificado> certificados = Arrays.asList(certificado);
        when(certificadoRepository.findByEventoId(1)).thenReturn(certificados);

        // Act
        List<Certificado> resultado = certificadoService.listarPorEvento(1);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(certificadoRepository, times(1)).findByEventoId(1);
    }

    @Test
    @DisplayName("Deve buscar certificado por hash com sucesso")
    void deveBuscarCertificadoPorHashComSucesso() {
        // Arrange
        when(certificadoRepository.findByHashCertificado("ABC123")).thenReturn(Optional.of(certificado));

        // Act
        Certificado resultado = certificadoService.buscarPorHash("ABC123");

        // Assert
        assertNotNull(resultado);
        assertEquals("ABC123", resultado.getHashCertificado());
        verify(certificadoRepository, times(1)).findByHashCertificado("ABC123");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar certificado inexistente por hash")
    void deveLancarExcecaoAoBuscarCertificadoInexistentePorHash() {
        // Arrange
        when(certificadoRepository.findByHashCertificado("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> certificadoService.buscarPorHash("INVALID")
        );
        assertEquals("Certificado não encontrado", exception.getReason());
    }

    @Test
    @DisplayName("Deve emitir certificado com sucesso")
    void deveEmitirCertificadoComSucesso() {
        // Arrange
        when(alunoRepository.findById(1)).thenReturn(Optional.of(aluno));
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(palestranteRepository.findById(1)).thenReturn(Optional.of(palestrante));
        when(certificadoRepository.save(any(Certificado.class))).thenReturn(certificado);

        // Act
        Certificado resultado = certificadoService.emitir(1, 1, 1, certificadoPayload);

        // Assert
        assertNotNull(resultado);
        verify(alunoRepository, times(1)).findById(1);
        verify(eventoRepository, times(1)).findById(1);
        verify(palestranteRepository, times(1)).findById(1);
        verify(certificadoRepository, times(1)).save(any(Certificado.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao emitir certificado com alunoId null")
    void deveLancarExcecaoAoEmitirCertificadoComAlunoIdNull() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> certificadoService.emitir(null, 1, 1, certificadoPayload)
        );
        assertEquals("ID do aluno não pode ser nulo", exception.getReason());
        verify(certificadoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao emitir certificado com eventoId null")
    void deveLancarExcecaoAoEmitirCertificadoComEventoIdNull() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> certificadoService.emitir(1, null, 1, certificadoPayload)
        );
        assertEquals("ID do evento não pode ser nulo", exception.getReason());
        verify(certificadoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao emitir certificado com palestranteId null")
    void deveLancarExcecaoAoEmitirCertificadoComPalestranteIdNull() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> certificadoService.emitir(1, 1, null, certificadoPayload)
        );
        assertEquals("ID do palestrante não pode ser nulo", exception.getReason());
        verify(certificadoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve gerar hash validador com sucesso")
    void deveGerarHashValidadorComSucesso() {
        // Act
        String hash = certificadoService.gerarHashValidador(aluno, evento, palestrante, "12345678901234");

        // Assert
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
        assertEquals(64, hash.length()); // SHA-256 produz 64 caracteres hexadecimais
    }

    @Test
    @DisplayName("Deve gerar certificados para evento com sucesso")
    void deveGerarCertificadosParaEventoComSucesso() {
        // Arrange
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(palestranteRepository.findById(1)).thenReturn(Optional.of(palestrante));
        when(inscricaoRepository.findByEventoIdAndPresenca(1, true))
            .thenReturn(Arrays.asList(inscricao));
        when(certificadoRepository.findByAlunoId(1)).thenReturn(Arrays.asList());
        when(certificadoRepository.save(any(Certificado.class))).thenReturn(certificado);

        // Act
        List<Certificado> resultado = certificadoService.gerarCertificadosParaEvento(
            1, 1, "UNINCOR", "12345678901234"
        );

        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(eventoRepository, times(1)).findById(1);
        verify(palestranteRepository, times(1)).findById(1);
        verify(inscricaoRepository, times(1)).findByEventoIdAndPresenca(1, true);
        verify(certificadoRepository, times(1)).save(any(Certificado.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao gerar certificados sem inscrições com presença")
    void deveLancarExcecaoAoGerarCertificadosSemInscricoesComPresenca() {
        // Arrange
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(palestranteRepository.findById(1)).thenReturn(Optional.of(palestrante));
        when(inscricaoRepository.findByEventoIdAndPresenca(1, true)).thenReturn(Arrays.asList());

        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> certificadoService.gerarCertificadosParaEvento(1, 1, "UNINCOR", "12345678901234")
        );
        assertEquals("Não há alunos com presença confirmada para este evento", exception.getReason());
    }

    @Test
    @DisplayName("Deve remover certificado com sucesso")
    void deveRemoverCertificadoComSucesso() {
        // Arrange
        doNothing().when(certificadoRepository).deleteById(1);

        // Act
        certificadoService.remover(1);

        // Assert
        verify(certificadoRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover certificado com ID null")
    void deveLancarExcecaoAoRemoverCertificadoComIdNull() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> certificadoService.remover(null)
        );
        assertEquals("ID não pode ser nulo", exception.getReason());
        verify(certificadoRepository, never()).deleteById(any());
    }
}

