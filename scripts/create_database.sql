-- OSManager Database Creation Script
-- Cria o banco de dados e as tabelas iniciais

-- scripts/create_database.sql

-- 1. Criar o banco de dados
CREATE DATABASE IF NOT EXISTS osmanager
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE osmanager;

-- 2. Criar tabela de clientes
CREATE TABLE IF NOT EXISTS clientes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    telefone VARCHAR(20),
    cpf_cnpj VARCHAR(20) UNIQUE,
    endereco VARCHAR(200),
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN DEFAULT TRUE
);

-- 3. Criar tabela de técnicos
CREATE TABLE IF NOT EXISTS tecnicos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    especialidade VARCHAR(50),
    data_contratacao DATE,
    salario DECIMAL(10,2),
    ativo BOOLEAN DEFAULT TRUE
);

-- 4. Criar tabela de ordens de serviço
CREATE TABLE IF NOT EXISTS ordens_servico (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cliente_id BIGINT NOT NULL,
    tecnico_id BIGINT,
    numero_os VARCHAR(20) UNIQUE,
    data_abertura TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_conclusao TIMESTAMP NULL,
    descricao_problema TEXT,
    solucao TEXT,
    status VARCHAR(20) DEFAULT 'ABERTA',
    prioridade VARCHAR(20) DEFAULT 'NORMAL',
    valor_mao_obra DECIMAL(10,2),
    FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    FOREIGN KEY (tecnico_id) REFERENCES tecnicos(id),
    INDEX idx_status (status),
    INDEX idx_cliente (cliente_id),
    INDEX idx_tecnico (tecnico_id)
);

-- 5. Criar tabela de serviços realizados
CREATE TABLE IF NOT EXISTS servicos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ordem_servico_id BIGINT NOT NULL,
    descricao VARCHAR(200) NOT NULL,
    quantidade INT DEFAULT 1,
    valor_unitario DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (ordem_servico_id) REFERENCES ordens_servico(id) ON DELETE CASCADE
);

-- 6. Criar tabela de peças utilizadas
CREATE TABLE IF NOT EXISTS pecas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ordem_servico_id BIGINT NOT NULL,
    codigo VARCHAR(50),
    descricao VARCHAR(100) NOT NULL,
    quantidade INT NOT NULL,
    valor_unitario DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (ordem_servico_id) REFERENCES ordens_servico(id) ON DELETE CASCADE
);

-- 7. Criar índices adicionais para performance
CREATE INDEX idx_servicos_os ON servicos(ordem_servico_id);
CREATE INDEX idx_pecas_os ON pecas(ordem_servico_id);

-- 8. Inserir dados de teste
INSERT INTO tecnicos (nome, email, especialidade, data_contratacao, salario) VALUES
('João Silva', 'joao@oficina.com', 'Mecânica Geral', '2023-01-15', 3500.00),
('Maria Santos', 'maria@oficina.com', 'Elétrica', '2023-02-01', 3800.00),
('Carlos Oliveira', 'carlos@oficina.com', 'Suspensão', '2023-03-10', 3600.00);

INSERT INTO clientes (nome, email, telefone, endereco) VALUES
('Empresa ABC Ltda', 'contato@abc.com', '(11) 99999-1111', 'Rua A, 123 - São Paulo, SP'),
('José Silva', 'jose@email.com', '(11) 99999-2222', 'Av. B, 456 - São Paulo, SP'),
('Maria Souza', 'maria@email.com', '(11) 99999-3333', 'Rua C, 789 - São Paulo, SP');