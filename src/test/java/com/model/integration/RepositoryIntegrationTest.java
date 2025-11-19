package com.model.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.model.domain.Aluno;
import com.model.domain.Certificado;
import com.model.domain.Colaborador;
import com.model.domain.Curso;
import com.model.domain.Evento;
import com.model.domain.Inscricao;
import com.model.domain.Palestrante;
import com.model.repository.AlunoRepository;
import com.model.repository.CertificadoRepository;
import com.model.repository.ColaboradorRepository;
import com.model.repository.CursoRepository;
import com.model.repository.EventoRepository;
import com.model.repository.InscricaoRepository;
import com.model.repository.PalestranteRepository;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes de Integração - Repositories com Banco de Dados")
class RepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private PalestranteRepository palestranteRepository;

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private CertificadoRepository certificadoRepository;

    @BeforeEach
    void setUp() {
        certificadoRepository.deleteAll();
        inscricaoRepository.deleteAll();
        alunoRepository.deleteAll();
        cursoRepository.deleteAll();
        eventoRepository.deleteAll();
        palestranteRepository.deleteAll();
        colaboradorRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve salvar e buscar Aluno usando herança JOINED")
    void deveSalvarEBuscarAlunoUsandoHerancaJoined() {
        // Arrange
        Aluno aluno = new Aluno();
        aluno.setNome("João Silva");
        aluno.setCpf("123.456.789-00");
        aluno.setEmail("joao@email.com");
        aluno.setPeriodo("4º Período");

        // Act
        Aluno salvo = alunoRepository.save(aluno);
        entityManager.flush();
        entityManager.clear();
        
        Optional<Aluno> encontrado = alunoRepository.findById(salvo.getId());

        // Assert
        assertTrue(encontrado.isPresent());
        assertEquals("João Silva", encontrado.get().getNome());
        assertEquals("4º Período", encontrado.get().getPeriodo());
        // Verifica que os dados de Pessoa também foram salvos
        assertEquals("123.456.789-00", encontrado.get().getCpf());
    }

    @Test
    @DisplayName("Deve salvar e buscar Palestrante usando herança JOINED")
    void deveSalvarEBuscarPalestranteUsandoHerancaJoined() {
        // Arrange
        Palestrante palestrante = new Palestrante();
        palestrante.setNome("Maria Santos");
        palestrante.setEmail("maria@email.com");
        palestrante.setDescricao("Especialista em Java");
        palestrante.setFotoUrl("https://example.com/foto.jpg");

        // Act
        Palestrante salvo = palestranteRepository.save(palestrante);
        entityManager.flush();
        entityManager.clear();
        
        Optional<Palestrante> encontrado = palestranteRepository.findById(salvo.getId());

        // Assert
        assertTrue(encontrado.isPresent());
        assertEquals("Maria Santos", encontrado.get().getNome());
        assertEquals("Especialista em Java", encontrado.get().getDescricao());
        assertEquals("https://example.com/foto.jpg", encontrado.get().getFotoUrl());
    }

    @Test
    @DisplayName("Deve salvar e buscar Colaborador usando herança JOINED")
    void deveSalvarEBuscarColaboradorUsandoHerancaJoined() {
        // Arrange
        Colaborador colaborador = new Colaborador();
        colaborador.setNome("Pedro Costa");
        colaborador.setEmail("pedro@email.com");
        colaborador.setCargo("Coordenador");

        // Act
        Colaborador salvo = colaboradorRepository.save(colaborador);
        entityManager.flush();
        entityManager.clear();
        
        Optional<Colaborador> encontrado = colaboradorRepository.findById(salvo.getId());

        // Assert
        assertTrue(encontrado.isPresent());
        assertEquals("Pedro Costa", encontrado.get().getNome());
        assertEquals("Coordenador", encontrado.get().getCargo());
    }

    @Test
    @DisplayName("Deve verificar relacionamento ManyToMany entre Aluno e Curso")
    void deveVerificarRelacionamentoManyToManyEntreAlunoECurso() {
        // Arrange
        Curso curso = new Curso();
        curso.setNome("Ciência da Computação");
        curso = cursoRepository.save(curso);

        Aluno aluno = new Aluno();
        aluno.setNome("João Silva");
        aluno.setCursos(new ArrayList<>());
        aluno.getCursos().add(curso);
        aluno = alunoRepository.save(aluno);

        entityManager.flush();
        entityManager.clear();

        // Act
        Aluno encontrado = alunoRepository.findById(aluno.getId()).orElse(null);

        // Assert
        assertNotNull(encontrado);
        assertNotNull(encontrado.getCursos());
        assertEquals(1, encontrado.getCursos().size());
        assertEquals("Ciência da Computação", encontrado.getCursos().get(0).getNome());
    }

    @Test
    @DisplayName("Deve verificar relacionamento ManyToMany entre Evento e Palestrante")
    void deveVerificarRelacionamentoManyToManyEntreEventoEPalestrante() {
        // Arrange
        Palestrante palestrante = new Palestrante();
        palestrante.setNome("Maria Santos");
        palestrante.setEmail("maria@email.com");
        palestrante = palestranteRepository.save(palestrante);

        Evento evento = new Evento();
        evento.setNome("Workshop Spring Boot");
        evento.setPalestrantes(new ArrayList<>());
        evento.getPalestrantes().add(palestrante);
        evento = eventoRepository.save(evento);

        entityManager.flush();
        entityManager.clear();

        // Act
        Evento encontrado = eventoRepository.findById(evento.getId()).orElse(null);

        // Assert
        assertNotNull(encontrado);
        assertNotNull(encontrado.getPalestrantes());
        assertEquals(1, encontrado.getPalestrantes().size());
        assertEquals("Maria Santos", encontrado.getPalestrantes().get(0).getNome());
    }

    @Test
    @DisplayName("Deve verificar relacionamento OneToMany entre Evento e Inscricao")
    void deveVerificarRelacionamentoOneToManyEntreEventoEInscricao() {
        // Arrange
        Aluno aluno = new Aluno();
        aluno.setNome("João Silva");
        aluno = alunoRepository.save(aluno);

        Evento evento = new Evento();
        evento.setNome("Workshop Spring Boot");
        evento.setInscricoes(new ArrayList<>());
        evento = eventoRepository.save(evento);

        Inscricao inscricao = new Inscricao();
        inscricao.setAluno(aluno);
        inscricao.setEvento(evento);
        inscricao.setPresenca(true);
        inscricao = inscricaoRepository.save(inscricao);

        entityManager.flush();
        entityManager.clear();

        // Act
        Evento encontrado = eventoRepository.findById(evento.getId()).orElse(null);

        // Assert
        assertNotNull(encontrado);
        assertNotNull(encontrado.getInscricoes());
        assertEquals(1, encontrado.getInscricoes().size());
    }

    @Test
    @DisplayName("Deve verificar relacionamento ManyToOne entre Certificado e Aluno/Evento/Palestrante")
    void deveVerificarRelacionamentoManyToOneEntreCertificadoEAlunoEventoPalestrante() {
        // Arrange
        Aluno aluno = new Aluno();
        aluno.setNome("João Silva");
        aluno = alunoRepository.save(aluno);

        Evento evento = new Evento();
        evento.setNome("Workshop Spring Boot");
        evento = eventoRepository.save(evento);

        Palestrante palestrante = new Palestrante();
        palestrante.setNome("Maria Santos");
        palestrante.setEmail("maria@email.com");
        palestrante = palestranteRepository.save(palestrante);

        Certificado certificado = new Certificado();
        certificado.setAluno(aluno);
        certificado.setEvento(evento);
        certificado.setPalestrante(palestrante);
        certificado.setHashCertificado("ABC123");
        certificado.setNomeInstituicao("UNINCOR");
        certificado = certificadoRepository.save(certificado);

        entityManager.flush();
        entityManager.clear();

        // Act
        Certificado encontrado = certificadoRepository.findById(certificado.getId()).orElse(null);

        // Assert
        assertNotNull(encontrado);
        assertNotNull(encontrado.getAluno());
        assertNotNull(encontrado.getEvento());
        assertNotNull(encontrado.getPalestrante());
        assertEquals("João Silva", encontrado.getAluno().getNome());
        assertEquals("Workshop Spring Boot", encontrado.getEvento().getNome());
        assertEquals("Maria Santos", encontrado.getPalestrante().getNome());
    }

    @Test
    @DisplayName("Deve verificar ElementCollection de categorias em Evento")
    void deveVerificarElementCollectionDeCategoriasEmEvento() {
        // Arrange
        Evento evento = new Evento();
        evento.setNome("Workshop Spring Boot");
        evento.setCategorias(Arrays.asList("Workshop", "Tecnologia", "Java"));
        evento = eventoRepository.save(evento);

        entityManager.flush();
        entityManager.clear();

        // Act
        Evento encontrado = eventoRepository.findById(evento.getId()).orElse(null);

        // Assert
        assertNotNull(encontrado);
        assertNotNull(encontrado.getCategorias());
        assertEquals(3, encontrado.getCategorias().size());
        assertTrue(encontrado.getCategorias().contains("Workshop"));
        assertTrue(encontrado.getCategorias().contains("Tecnologia"));
        assertTrue(encontrado.getCategorias().contains("Java"));
    }

    @Test
    @DisplayName("Deve buscar eventos por categoria usando repository")
    void deveBuscarEventosPorCategoriaUsandoRepository() {
        // Arrange
        Evento evento1 = new Evento();
        evento1.setNome("Workshop Spring Boot");
        evento1.setCategorias(Arrays.asList("Workshop", "Tecnologia"));
        eventoRepository.save(evento1);

        Evento evento2 = new Evento();
        evento2.setNome("Palestra Java");
        evento2.setCategorias(Arrays.asList("Palestra", "Tecnologia"));
        eventoRepository.save(evento2);

        entityManager.flush();
        entityManager.clear();

        // Act
        List<Evento> resultado = eventoRepository.findByCategoriasContainingIgnoreCase("Workshop");

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Workshop Spring Boot", resultado.get(0).getNome());
    }

    @Test
    @DisplayName("Deve verificar existsByCpf em AlunoRepository")
    void deveVerificarExistsByCpfEmAlunoRepository() {
        // Arrange
        Aluno aluno = new Aluno();
        aluno.setNome("João Silva");
        aluno.setCpf("123.456.789-00");
        aluno = alunoRepository.save(aluno);

        entityManager.flush();
        entityManager.clear();

        // Act
        boolean existe = alunoRepository.existsByCpf("123.456.789-00");
        boolean naoExiste = alunoRepository.existsByCpf("999.999.999-99");

        // Assert
        assertTrue(existe);
        assertFalse(naoExiste);
    }

    @Test
    @DisplayName("Deve verificar existsByNomeIgnoreCase em CursoRepository")
    void deveVerificarExistsByNomeIgnoreCaseEmCursoRepository() {
        // Arrange
        Curso curso = new Curso();
        curso.setNome("Ciência da Computação");
        cursoRepository.save(curso);

        entityManager.flush();
        entityManager.clear();

        // Act
        boolean existe = cursoRepository.existsByNomeIgnoreCase("ciência da computação");
        boolean naoExiste = cursoRepository.existsByNomeIgnoreCase("Engenharia de Software");

        // Assert
        assertTrue(existe);
        assertFalse(naoExiste);
    }

    @Test
    @DisplayName("Deve verificar existsByEventoIdAndAlunoId em InscricaoRepository")
    void deveVerificarExistsByEventoIdAndAlunoIdEmInscricaoRepository() {
        // Arrange
        Aluno aluno = new Aluno();
        aluno.setNome("João Silva");
        aluno = alunoRepository.save(aluno);

        Evento evento = new Evento();
        evento.setNome("Workshop Spring Boot");
        evento = eventoRepository.save(evento);

        Inscricao inscricao = new Inscricao();
        inscricao.setAluno(aluno);
        inscricao.setEvento(evento);
        inscricaoRepository.save(inscricao);

        entityManager.flush();
        entityManager.clear();

        // Act
        boolean existe = inscricaoRepository.existsByEventoIdAndAlunoId(evento.getId(), aluno.getId());
        boolean naoExiste = inscricaoRepository.existsByEventoIdAndAlunoId(999, aluno.getId());

        // Assert
        assertTrue(existe);
        assertFalse(naoExiste);
    }

    @Test
    @DisplayName("Deve buscar inscrições por evento e presença")
    void deveBuscarInscricoesPorEventoEPresenca() {
        // Arrange
        Aluno aluno1 = new Aluno();
        aluno1.setNome("João Silva");
        aluno1 = alunoRepository.save(aluno1);

        Aluno aluno2 = new Aluno();
        aluno2.setNome("Maria Santos");
        aluno2 = alunoRepository.save(aluno2);

        Evento evento = new Evento();
        evento.setNome("Workshop Spring Boot");
        evento = eventoRepository.save(evento);

        Inscricao inscricao1 = new Inscricao();
        inscricao1.setAluno(aluno1);
        inscricao1.setEvento(evento);
        inscricao1.setPresenca(true);
        inscricaoRepository.save(inscricao1);

        Inscricao inscricao2 = new Inscricao();
        inscricao2.setAluno(aluno2);
        inscricao2.setEvento(evento);
        inscricao2.setPresenca(false);
        inscricaoRepository.save(inscricao2);

        entityManager.flush();
        entityManager.clear();

        // Act
        List<Inscricao> comPresenca = inscricaoRepository.findByEventoIdAndPresenca(evento.getId(), true);
        List<Inscricao> semPresenca = inscricaoRepository.findByEventoIdAndPresenca(evento.getId(), false);

        // Assert
        assertNotNull(comPresenca);
        assertEquals(1, comPresenca.size());
        assertEquals("João Silva", comPresenca.get(0).getAluno().getNome());
        
        assertNotNull(semPresenca);
        assertEquals(1, semPresenca.size());
        assertEquals("Maria Santos", semPresenca.get(0).getAluno().getNome());
    }
}

