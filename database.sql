CREATE DATABASE IF NOT EXISTS meu_faturamento CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE meu_faturamento;

CREATE TABLE IF NOT EXISTS clientes (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nome VARCHAR(100) NOT NULL,
  telefone VARCHAR(20) DEFAULT NULL,
  observacao TEXT DEFAULT NULL,
  criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS servicos (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nome VARCHAR(80) NOT NULL,
  icone VARCHAR(20) DEFAULT '✂️',
  preco DECIMAL(8,2) NOT NULL,
  ativo TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS atendimentos (
  id BIGINT NOT NULL AUTO_INCREMENT,
  cliente_id BIGINT DEFAULT NULL,
  nome_avulso VARCHAR(100) DEFAULT NULL,
  data_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
  total DECIMAL(8,2) NOT NULL DEFAULT 0.00,
  observacao TEXT DEFAULT NULL,
  forma_pagamento VARCHAR(20) DEFAULT 'dinheiro',
  PRIMARY KEY (id),
  CONSTRAINT fk_atend_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS atendimento_servicos (
  atendimento_id BIGINT NOT NULL,
  servico_id BIGINT NOT NULL,
  PRIMARY KEY (atendimento_id, servico_id),
  CONSTRAINT fk_as_atend FOREIGN KEY (atendimento_id) REFERENCES atendimentos(id) ON DELETE CASCADE,
  CONSTRAINT fk_as_servico FOREIGN KEY (servico_id) REFERENCES servicos(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO servicos (id, nome, icone, preco) VALUES
  (1, 'Cabelo',      '✂️', 40.00),
  (2, 'Bigode',      '🧔', 15.00),
  (3, 'Barba',       '🪒', 15.00),
  (4, 'Sobrancelha', '💆',  5.00),
  (5, 'Tintura',     '🧴', 30.00);

-- Tabela de usuários para login
CREATE TABLE IF NOT EXISTS usuarios (
  id BIGINT NOT NULL AUTO_INCREMENT,
  usuario VARCHAR(60) NOT NULL UNIQUE,
  senha VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Usuários são criados pela própria tela inicial (botão "Criar conta").
