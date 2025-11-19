package com.model.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.model.domain.Aluno;
import com.model.repository.AlunoRepository;
import com.model.services.AlunoService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - AlunoService com Banco de Dados")
class AlunoServiceIntegrationTest {

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private AlunoRepository alunoRepository;

    private Aluno aluno;

    @BeforeEach
    void setUp() {
        // Limpa dados antes de cada teste
        alunoRepository.deleteAll();
        
        aluno = new Aluno();
        aluno.setNome("João Silva");
        aluno.setCpf("123.456.789-00");
        aluno.setEmail("joao@email.com");
        aluno.setContato("(11) 98765-4321");
        aluno.setDataNascimento(new Date());
        aluno.setPeriodo("4º Período");
    }

    @Test
    @DisplayName("Deve criar e salvar aluno no banco de dados")
    void deveCriarESalvarAlunoNoBancoDeDados() {
        // Act
        Aluno resultado = alunoService.criar(aluno);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals("João Silva", resultado.getNome());
        assertEquals("123.456.789-00", resultado.getCpf());
        
        // Verifica que foi salvo no banco
        Aluno salvo = alunoRepository.findById(resultado.getId()).orElse(null);
        assertNotNull(salvo);
        assertEquals("João Silva", salvo.getNome());
    }

    @Test
    @DisplayName("Deve buscar aluno do banco de dados por ID")
    void deveBuscarAlunoDoBancoDeDadosPorId() {
        // Arrange
        Aluno criado = alunoService.criar(aluno);
        Integer id = criado.getId();

        // Act
        Aluno resultado = alunoService.buscarPorId(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("João Silva", resultado.getNome());
    }

    @Test
    @DisplayName("Deve listar todos os alunos do banco de dados")
    void deveListarTodosOsAlunosDoBancoDeDados() {
        // Arrange
        alunoService.criar(aluno);
        
        Aluno aluno2 = new Aluno();
        aluno2.setNome("Maria Santos");
        aluno2.setCpf("987.654.321-00");
        aluno2.setEmail("maria@email.com");
        alunoService.criar(aluno2);

        // Act
        List<Aluno> resultado = alunoService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Deve atualizar aluno no banco de dados")
    void deveAtualizarAlunoNoBancoDeDados() {
        // Arrange
        Aluno criado = alunoService.criar(aluno);
        Integer id = criado.getId();
        
        Aluno atualizado = new Aluno();
        atualizado.setNome("João Silva Santos");
        atualizado.setCpf("123.456.789-00");
        atualizado.setEmail("joao.santos@email.com");
        atualizado.setPeriodo("5º Período");

        // Act
        Aluno resultado = alunoService.atualizar(id, atualizado);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("João Silva Santos", resultado.getNome());
        assertEquals("5º Período", resultado.getPeriodo());
        
        // Verifica no banco
        Aluno doBanco = alunoRepository.findById(id).orElse(null);
        assertNotNull(doBanco);
        assertEquals("João Silva Santos", doBanco.getNome());
    }

    @Test
    @DisplayName("Deve remover aluno do banco de dados")
    void deveRemoverAlunoDoBancoDeDados() {
        // Arrange
        Aluno criado = alunoService.criar(aluno);
        Integer id = criado.getId();

        // Act
        alunoService.remover(id);

        // Assert
        assertFalse(alunoRepository.existsById(id));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar aluno com CPF duplicado")
    void deveLancarExcecaoAoCriarAlunoComCpfDuplicado() {
        // Arrange
        alunoService.criar(aluno);
        
        Aluno alunoDuplicado = new Aluno();
        alunoDuplicado.setNome("Outro Aluno");
        alunoDuplicado.setCpf("123.456.789-00"); // Mesmo CPF
        alunoDuplicado.setEmail("outro@email.com");

        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> alunoService.criar(alunoDuplicado)
        );
        assertEquals("CPF já cadastrado", exception.getReason());
        
        // Verifica que só existe um aluno no banco
        assertEquals(1, alunoRepository.count());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar aluno inexistente")
    void deveLancarExcecaoAoBuscarAlunoInexistente() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> alunoService.buscarPorId(999)
        );
        assertEquals("Aluno não encontrado", exception.getReason());
    }
}

