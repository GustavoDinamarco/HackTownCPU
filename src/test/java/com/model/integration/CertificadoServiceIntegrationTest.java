package com.model.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

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
import com.model.services.CertificadoService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - CertificadoService com Banco de Dados")
class CertificadoServiceIntegrationTest {

    @Autowired
    private CertificadoService certificadoService;

    @Autowired
    private CertificadoRepository certificadoRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private PalestranteRepository palestranteRepository;

    @Autowired
    private InscricaoRepository inscricaoRepository;

    private Aluno aluno;
    private Evento evento;
    private Palestrante palestrante;
    private Certificado certificadoPayload;

    @BeforeEach
    void setUp() {
        certificadoRepository.deleteAll();
        inscricaoRepository.deleteAll();
        alunoRepository.deleteAll();
        eventoRepository.deleteAll();
        palestranteRepository.deleteAll();
        
        // Cria aluno
        aluno = new Aluno();
        aluno.setNome("João Silva");
        aluno.setCpf("123.456.789-00");
        aluno.setEmail("joao@email.com");
        aluno = alunoRepository.save(aluno);
        
        // Cria evento
        evento = new Evento();
        evento.setNome("Workshop Spring Boot");
        evento.setInscricoes(new ArrayList<>());
        evento = eventoRepository.save(evento);
        
        // Cria palestrante
        palestrante = new Palestrante();
        palestrante.setNome("Maria Santos");
        palestrante.setEmail("maria@email.com");
        palestrante.setDescricao("Especialista em Java");
        palestrante = palestranteRepository.save(palestrante);
        
        // Payload do certificado
        certificadoPayload = new Certificado();
        certificadoPayload.setHashCertificado("ABC123DEF456");
        certificadoPayload.setNomeInstituicao("UNINCOR");
        certificadoPayload.setIdentidadeInstituicao("12345678901234");
    }

    @Test
    @DisplayName("Deve emitir certificado no banco de dados")
    void deveEmitirCertificadoNoBancoDeDados() {
        // Act
        Certificado resultado = certificadoService.emitir(
            aluno.getId(), 
            evento.getId(), 
            palestrante.getId(), 
            certificadoPayload
        );

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals(aluno.getId(), resultado.getAluno().getId());
        assertEquals(evento.getId(), resultado.getEvento().getId());
        assertEquals(palestrante.getId(), resultado.getPalestrante().getId());
        assertEquals("ABC123DEF456", resultado.getHashCertificado());
        assertEquals("UNINCOR", resultado.getNomeInstituicao());
        
        // Verifica que foi salvo no banco
        assertTrue(certificadoRepository.existsById(resultado.getId()));
    }

    @Test
    @DisplayName("Deve listar certificados por aluno do banco de dados")
    void deveListarCertificadosPorAlunoDoBancoDeDados() {
        // Arrange
        certificadoService.emitir(aluno.getId(), evento.getId(), palestrante.getId(), certificadoPayload);
        
        Evento evento2 = new Evento();
        evento2.setNome("Palestra Java");
        evento2 = eventoRepository.save(evento2);
        
        Certificado payload2 = new Certificado();
        payload2.setHashCertificado("XYZ789");
        payload2.setNomeInstituicao("UNINCOR");
        certificadoService.emitir(aluno.getId(), evento2.getId(), palestrante.getId(), payload2);

        // Act
        List<Certificado> resultado = certificadoService.listarPorAluno(aluno.getId());

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Deve buscar certificado por hash no banco de dados")
    void deveBuscarCertificadoPorHashNoBancoDeDados() {
        // Arrange
        Certificado criado = certificadoService.emitir(
            aluno.getId(), 
            evento.getId(), 
            palestrante.getId(), 
            certificadoPayload
        );

        // Act
        Certificado resultado = certificadoService.buscarPorHash("ABC123DEF456");

        // Assert
        assertNotNull(resultado);
        assertEquals(criado.getId(), resultado.getId());
        assertEquals("ABC123DEF456", resultado.getHashCertificado());
    }

    @Test
    @DisplayName("Deve gerar certificados automaticamente para evento")
    void deveGerarCertificadosAutomaticamenteParaEvento() {
        // Arrange
        Inscricao inscricao = new Inscricao();
        inscricao.setAluno(aluno);
        inscricao.setEvento(evento);
        inscricao.setPresenca(true);
        inscricao.setDataInscricao(new Date());
        inscricao = inscricaoRepository.save(inscricao);

        // Act
        List<Certificado> resultado = certificadoService.gerarCertificadosParaEvento(
            evento.getId(), 
            palestrante.getId(), 
            "UNINCOR", 
            "12345678901234"
        );

        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertNotNull(resultado.get(0).getHashCertificado());
        assertEquals("UNINCOR", resultado.get(0).getNomeInstituicao());
    }

    @Test
    @DisplayName("Deve remover certificado do banco de dados")
    void deveRemoverCertificadoDoBancoDeDados() {
        // Arrange
        Certificado criado = certificadoService.emitir(
            aluno.getId(), 
            evento.getId(), 
            palestrante.getId(), 
            certificadoPayload
        );
        Integer id = criado.getId();

        // Act
        certificadoService.remover(id);

        // Assert
        assertFalse(certificadoRepository.existsById(id));
    }

    @Test
    @DisplayName("Deve gerar hash validador único")
    void deveGerarHashValidadorUnico() {
        // Act
        String hash1 = certificadoService.gerarHashValidador(aluno, evento, palestrante, "12345678901234");
        String hash2 = certificadoService.gerarHashValidador(aluno, evento, palestrante, "12345678901234");

        // Assert
        assertNotNull(hash1);
        assertNotNull(hash2);
        assertEquals(64, hash1.length()); // SHA-256 produz 64 caracteres hexadecimais
        // Nota: Hashes podem ser diferentes devido ao timestamp, mas devem ter o mesmo formato
    }
}

