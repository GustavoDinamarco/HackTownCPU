-- Tabelas base
CREATE TABLE cursos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL UNIQUE
);

CREATE TABLE pessoas (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    cpf VARCHAR(20),
    contato VARCHAR(80),
    email VARCHAR(120),
    data_nascimento DATE
);

CREATE TABLE alunos (
    id INTEGER PRIMARY KEY REFERENCES pessoas(id) ON DELETE CASCADE,
    periodo VARCHAR(20)
);

CREATE TABLE colaboradores (
    id INTEGER PRIMARY KEY REFERENCES pessoas(id) ON DELETE CASCADE,
    cargo VARCHAR(60)
);

CREATE TABLE palestrantes (
    id INTEGER PRIMARY KEY REFERENCES pessoas(id) ON DELETE CASCADE,
    descricao TEXT,
    foto_url VARCHAR(255)
);

CREATE TABLE eventos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    local VARCHAR(150),
    hora_inicio TIMESTAMP,
    hora_fim TIMESTAMP,
    descricao TEXT,
    carga_horaria INTEGER,
    vagas INTEGER,
    banner_url VARCHAR(255)
);

CREATE TABLE evento_categorias (
    evento_id INTEGER REFERENCES eventos(id) ON DELETE CASCADE,
    categoria VARCHAR(80),
    PRIMARY KEY (evento_id, categoria)
);

-- Relações aluno-curso / evento-curso / evento-palestrante
CREATE TABLE aluno_curso (
    aluno_id INTEGER REFERENCES alunos(id) ON DELETE CASCADE,
    curso_id INTEGER REFERENCES cursos(id) ON DELETE CASCADE,
    PRIMARY KEY (aluno_id, curso_id)
);

CREATE TABLE evento_curso (
    evento_id INTEGER REFERENCES eventos(id) ON DELETE CASCADE,
    curso_id INTEGER REFERENCES cursos(id) ON DELETE CASCADE,
    PRIMARY KEY (evento_id, curso_id)
);

CREATE TABLE evento_palestrante (
    evento_id INTEGER REFERENCES eventos(id) ON DELETE CASCADE,
    palestrante_id INTEGER REFERENCES palestrantes(id) ON DELETE CASCADE,
    PRIMARY KEY (evento_id, palestrante_id)
);

-- Inscrições
CREATE TABLE inscricoes (
    id SERIAL PRIMARY KEY,
    aluno_id INTEGER NOT NULL REFERENCES alunos(id) ON DELETE CASCADE,
    evento_id INTEGER NOT NULL REFERENCES eventos(id) ON DELETE CASCADE,
    data_inscricao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    presenca BOOLEAN
);

CREATE UNIQUE INDEX ux_inscricao_evento_aluno
    ON inscricoes (evento_id, aluno_id);

-- Certificados
CREATE TABLE certificados (
    id SERIAL PRIMARY KEY,
    aluno_id INTEGER NOT NULL REFERENCES alunos(id) ON DELETE CASCADE,
    evento_id INTEGER NOT NULL REFERENCES eventos(id) ON DELETE CASCADE,
    palestrante_id INTEGER NOT NULL REFERENCES palestrantes(id) ON DELETE CASCADE,
    hash_certificado VARCHAR(120) NOT NULL UNIQUE,
    nome_instituicao VARCHAR(150),
    identidade_instituicao VARCHAR(80)
);