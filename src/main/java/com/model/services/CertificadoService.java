package com.model.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificadoService {

    private final CertificadoRepository certificadoRepository;
    private final AlunoRepository alunoRepository;
    private final EventoRepository eventoRepository;
    private final PalestranteRepository palestranteRepository;
    private final InscricaoRepository inscricaoRepository;

    public List<Certificado> listarTodos() {
        return certificadoRepository.findAll();
    }

    public List<Certificado> listarPorAluno(Integer alunoId) {
        return certificadoRepository.findByAlunoId(alunoId);
    }

    public List<Certificado> listarPorEvento(Integer eventoId) {
        return certificadoRepository.findByEventoId(eventoId);
    }

    public Certificado buscarPorHash(String hash) {
        return certificadoRepository.findByHashCertificado(hash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificado não encontrado"));
    }

    public Certificado emitir(Integer alunoId, Integer eventoId, Integer palestranteId, Certificado certificadoPayload) {
        if (alunoId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do aluno não pode ser nulo");
        }
        if (eventoId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do evento não pode ser nulo");
        }
        if (palestranteId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do palestrante não pode ser nulo");
        }
        
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));
        Palestrante palestrante = palestranteRepository.findById(palestranteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Palestrante não encontrado"));

        Certificado certificado = new Certificado();
        certificado.setAluno(aluno);
        certificado.setEvento(evento);
        certificado.setPalestrante(palestrante);
        certificado.setHashCertificado(certificadoPayload.getHashCertificado());
        certificado.setNomeInstituicao(certificadoPayload.getNomeInstituicao());
        certificado.setIdentidadeInstituicao(certificadoPayload.getIdentidadeInstituicao());
        return certificadoRepository.save(certificado);
    }

    public void remover(Integer id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID não pode ser nulo");
        }
        certificadoRepository.deleteById(id);
    }

    /**
     * Gera uma hash validador única para um certificado.
     * A hash é baseada em informações do aluno, evento, palestrante e timestamp,
     * garantindo unicidade e segurança para validação do certificado.
     * 
     * @param aluno O aluno que receberá o certificado
     * @param evento O evento para o qual o certificado será emitido
     * @param palestrante O palestrante do evento
     * @param identidadeInstituicao A identidade da instituição (opcional, mas recomendado)
     * @return Uma hash SHA-256 em formato hexadecimal (64 caracteres)
     * @throws RuntimeException se ocorrer erro ao gerar a hash
     */
    public String gerarHashValidador(Aluno aluno, Evento evento, Palestrante palestrante, String identidadeInstituicao) {
        try {
            // Constrói uma string única com informações do certificado
            StringBuilder dados = new StringBuilder();
            dados.append(aluno.getId()).append("|");
            dados.append(aluno.getNome() != null ? aluno.getNome() : "").append("|");
            dados.append(aluno.getCpf() != null ? aluno.getCpf() : "").append("|");
            dados.append(evento.getId()).append("|");
            dados.append(evento.getNome() != null ? evento.getNome() : "").append("|");
            dados.append(palestrante.getId()).append("|");
            dados.append(palestrante.getNome() != null ? palestrante.getNome() : "").append("|");
            dados.append(identidadeInstituicao != null ? identidadeInstituicao : "").append("|");
            dados.append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("|");
            dados.append("CERTIFICADO_VALIDATOR_KEY"); // Chave secreta para aumentar segurança

            // Gera hash SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(dados.toString().getBytes(StandardCharsets.UTF_8));

            // Converte para hexadecimal
            StringBuilder hashHex = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hashHex.append('0');
                }
                hashHex.append(hex);
            }

            return hashHex.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash do certificado: algoritmo SHA-256 não disponível", e);
        }
    }

    /**
     * Gera uma hash validador para um certificado já criado.
     * 
     * @param certificado O certificado para o qual gerar a hash
     * @return Uma hash SHA-256 em formato hexadecimal (64 caracteres)
     */
    public String gerarHashValidador(Certificado certificado) {
        return gerarHashValidador(
            certificado.getAluno(),
            certificado.getEvento(),
            certificado.getPalestrante(),
            certificado.getIdentidadeInstituicao()
        );
    }

    /**
     * Gera certificados automaticamente para todos os alunos inscritos em um evento
     * que tiveram sua presença confirmada. A hash validador é gerada automaticamente.
     * 
     * @param eventoId ID do evento
     * @param palestranteId ID do palestrante principal (será usado para todos os certificados)
     * @param nomeInstituicao Nome da instituição
     * @param identidadeInstituicao Identidade/CNPJ da instituição
     * @return Lista de certificados gerados
     * @throws ResponseStatusException se evento, palestrante ou inscrições não forem encontrados
     */
    public List<Certificado> gerarCertificadosParaEvento(
            Integer eventoId, 
            Integer palestranteId, 
            String nomeInstituicao, 
            String identidadeInstituicao) {
        
        if (eventoId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do evento não pode ser nulo");
        }
        if (palestranteId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do palestrante não pode ser nulo");
        }
        
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));
        
        Palestrante palestrante = palestranteRepository.findById(palestranteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Palestrante não encontrado"));

        // Busca todas as inscrições com presença confirmada (presenca = true)
        List<Inscricao> inscricoesComPresenca = inscricaoRepository.findByEventoIdAndPresenca(eventoId, true);

        if (inscricoesComPresenca.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Não há alunos com presença confirmada para este evento");
        }

        return inscricoesComPresenca.stream()
            .map(inscricao -> {
                // Verifica se já existe certificado para este aluno e evento
                boolean jaExisteCertificado = certificadoRepository.findByAlunoId(inscricao.getAluno().getId())
                    .stream()
                    .anyMatch(cert -> cert.getEvento().getId().equals(eventoId));

                if (jaExisteCertificado) {
                    return null; // Pula se já existe certificado
                }

                Certificado certificado = new Certificado();
                certificado.setAluno(inscricao.getAluno());
                certificado.setEvento(evento);
                certificado.setPalestrante(palestrante);
                certificado.setNomeInstituicao(nomeInstituicao);
                certificado.setIdentidadeInstituicao(identidadeInstituicao);
                
                // Gera a hash validador automaticamente
                String hash = gerarHashValidador(
                    inscricao.getAluno(), 
                    evento, 
                    palestrante, 
                    identidadeInstituicao
                );
                certificado.setHashCertificado(hash);

                return certificadoRepository.save(certificado);
            })
            .filter(certificado -> certificado != null)
            .collect(Collectors.toList());
    }
}