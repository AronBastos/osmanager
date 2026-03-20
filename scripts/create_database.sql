-- OSManager Database Creation Script
-- Cria o banco de dados e as tabelas iniciais

-- Criar banco de dados
CREATE DATABASE IF NOT EXISTS `osmanager` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `osmanager`;

-- ============================================
-- Tabela: clientes
-- ============================================
CREATE TABLE `clientes` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `nome` VARCHAR(150) NOT NULL,
    `email` VARCHAR(100),
    `telefone` VARCHAR(20),
    `cpf` VARCHAR(14) UNIQUE,
    `endereco` VARCHAR(255),
    `cidade` VARCHAR(100),
    `estado` VARCHAR(2),
    `cep` VARCHAR(10),
    `data_cadastro` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `ativo` BOOLEAN DEFAULT TRUE,
    INDEX `idx_nome` (`nome`),
    INDEX `idx_email` (`email`),
    INDEX `idx_cpf` (`cpf`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Tabela: veiculo
-- ============================================
CREATE TABLE `veiculo` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `cliente_id` BIGINT NOT NULL,
    `placa` VARCHAR(10) UNIQUE NOT NULL,
    `marca` VARCHAR(50) NOT NULL,
    `modelo` VARCHAR(100) NOT NULL,
    `ano` INT,
    `cor` VARCHAR(50),
    `km` DECIMAL(10, 2),
    `observacoes` TEXT,
    `data_cadastro` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `ativo` BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`) ON DELETE CASCADE,
    INDEX `idx_placa` (`placa`),
    INDEX `idx_cliente_id` (`cliente_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Tabela: tipo_servico
-- ============================================
CREATE TABLE `tipo_servico` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `nome` VARCHAR(100) NOT NULL UNIQUE,
    `descricao` TEXT,
    `valor_base` DECIMAL(10, 2),
    `data_cadastro` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `ativo` BOOLEAN DEFAULT TRUE,
    INDEX `idx_nome` (`nome`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Tabela: ordem_servico
-- ============================================
CREATE TABLE `ordem_servico` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `numero_os` VARCHAR(20) UNIQUE NOT NULL,
    `cliente_id` BIGINT NOT NULL,
    `veiculo_id` BIGINT NOT NULL,
    `descricao_problema` TEXT NOT NULL,
    `observacoes` TEXT,
    `status` VARCHAR(20) DEFAULT 'ABERTA',
    `valor_total` DECIMAL(10, 2),
    `data_abertura` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `data_conclusao` TIMESTAMP NULL,
    `data_entrega` TIMESTAMP NULL,
    `criado_em` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `atualizado_em` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`) ON DELETE RESTRICT,
    FOREIGN KEY (`veiculo_id`) REFERENCES `veiculo` (`id`) ON DELETE RESTRICT,
    INDEX `idx_numero_os` (`numero_os`),
    INDEX `idx_status` (`status`),
    INDEX `idx_cliente_id` (`cliente_id`),
    INDEX `idx_veiculo_id` (`veiculo_id`),
    INDEX `idx_data_abertura` (`data_abertura`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Tabela: item_ordem_servico
-- ============================================
CREATE TABLE `item_ordem_servico` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `ordem_servico_id` BIGINT NOT NULL,
    `tipo_servico_id` BIGINT NOT NULL,
    `descricao` TEXT,
    `quantidade` DECIMAL(10, 2) DEFAULT 1,
    `valor_unitario` DECIMAL(10, 2) NOT NULL,
    `valor_total` DECIMAL(10, 2) GENERATED ALWAYS AS (quantidade * valor_unitario) STORED,
    `data_criacao` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`ordem_servico_id`) REFERENCES `ordem_servico` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`tipo_servico_id`) REFERENCES `tipo_servico` (`id`) ON DELETE RESTRICT,
    INDEX `idx_ordem_servico_id` (`ordem_servico_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- Inserir dados de exemplo
-- ============================================

-- Inserir tipos de serviço
INSERT INTO `tipo_servico` (`nome`, `descricao`, `valor_base`) VALUES
('Troca de Óleo', 'Serviço de troca de óleo do motor', 50.00),
('Revisão Geral', 'Revisão completa do veículo', 150.00),
('Alinhamento', 'Alinhamento das rodas', 80.00),
('Balanceamento', 'Balanceamento de rodas', 60.00),
('Freios', 'Revisão e manutenção do sistema de freios', 120.00),
('Suspenção', 'Revisão da suspenção', 200.00);

-- Inserir cliente de exemplo
INSERT INTO `clientes` (`nome`, `email`, `telefone`, `cpf`) VALUES
('João Silva', 'joao@email.com', '(11) 98765-4321', '12345678901');

-- Inserir veículo de exemplo
INSERT INTO `veiculo` (`cliente_id`, `placa`, `marca`, `modelo`, `ano`, `cor`) VALUES
(1, 'ABC-1234', 'Toyota', 'Corolla', 2020, 'Prata');

-- Criar índices adicionais para otimização
CREATE INDEX `idx_ordem_veiculo_cliente` ON `ordem_servico` (`veiculo_id`, `cliente_id`);
CREATE INDEX `idx_item_tipo_servico` ON `item_ordem_servico` (`tipo_servico_id`);

