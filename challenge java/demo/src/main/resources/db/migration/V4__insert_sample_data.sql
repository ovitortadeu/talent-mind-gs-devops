-- Inserindo dados de exemplo para a aplicação

-- Estados
INSERT INTO TB_MTT_ESTADO (sigla_estado, nome_estado) VALUES ('SP', 'São Paulo');
INSERT INTO TB_MTT_ESTADO (sigla_estado, nome_estado) VALUES ('RJ', 'Rio de Janeiro');
INSERT INTO TB_MTT_ESTADO (sigla_estado, nome_estado) VALUES ('MG', 'Minas Gerais');
INSERT INTO TB_MTT_ESTADO (sigla_estado, nome_estado) VALUES ('BA', 'Bahia');
INSERT INTO TB_MTT_ESTADO (sigla_estado, nome_estado) VALUES ('PR', 'Paraná');

-- Cidades
INSERT INTO TB_MTT_CIDADE (TB_MTT_ESTADO_id, nome_cidade, numero_ddd) VALUES (1, 'São Paulo', 11);
INSERT INTO TB_MTT_CIDADE (TB_MTT_ESTADO_id, nome_cidade, numero_ddd) VALUES (2, 'Rio de Janeiro', 21);
INSERT INTO TB_MTT_CIDADE (TB_MTT_ESTADO_id, nome_cidade, numero_ddd) VALUES (3, 'Belo Horizonte', 31);
INSERT INTO TB_MTT_CIDADE (TB_MTT_ESTADO_id, nome_cidade, numero_ddd) VALUES (4, 'Salvador', 71);
INSERT INTO TB_MTT_CIDADE (TB_MTT_ESTADO_id, nome_cidade, numero_ddd) VALUES (1, 'Campinas', 19);

-- Filiais
INSERT INTO TB_MTT_FILIAL (id) VALUES (DEFAULT);
INSERT INTO TB_MTT_FILIAL (id) VALUES (DEFAULT);
INSERT INTO TB_MTT_FILIAL (id) VALUES (DEFAULT);

-- Senha para 'admin' é 'admin123'
INSERT INTO TB_MTT_USUARIO (username, email, senha, role) VALUES ('admin', 'admin@mottu.com', '$2a$10$QcNYXeLct7FZKvPxSd/dwe2lSl5gH6Gz.EVJ2m4bdFIByfwSw8bFS', 'ADMIN');
INSERT INTO TB_MTT_USUARIO (username, email, senha, role) VALUES ('user', 'user@mottu.com', '$2a$10$debnHh68p8wTdJq6Lohqlej5jehfin0SB45LqQLqZlzL9T6SYwUBG', 'USER');
INSERT INTO TB_MTT_USUARIO (username, email, senha, role) VALUES ('flima', 'fernanda.lima@example.com', '$2a$10$E2upv52Y6s.33e4L6tVv3.42c23U0.Ie9.n23uA0A6z5A2g1g5G3e', 'USER');

-- Veículos (associados aos usuários criados)
INSERT INTO TB_MTT_VEICULO (TB_MTT_USUARIO_id, placa_antiga, placa_nova, tipo_veiculo) VALUES ((SELECT id FROM TB_MTT_USUARIO WHERE username = 'user'), 'ABC1234', 'BRA1A23', 'Motocicleta');
INSERT INTO TB_MTT_VEICULO (TB_MTT_USUARIO_id, placa_antiga, placa_nova, tipo_veiculo) VALUES ((SELECT id FROM TB_MTT_USUARIO WHERE username = 'flima'), NULL, 'MER2B34', 'Automóvel Hatch');
INSERT INTO TB_MTT_VEICULO (TB_MTT_USUARIO_id, placa_antiga, placa_nova, tipo_veiculo) VALUES ((SELECT id FROM TB_MTT_USUARIO WHERE username = 'user'), 'XYZ7890', 'COS3C45', 'Automóvel Sedan');

-- Câmeras
INSERT INTO TB_MTT_CAMERA (TB_MTT_FILIAL_id, modelo) VALUES (1, 'Intelbras VIP 3230 B');
INSERT INTO TB_MTT_CAMERA (TB_MTT_FILIAL_id, modelo) VALUES (2, 'Hikvision DS-2CD2143G0-IS');

-- IoT
INSERT INTO TB_MTT_IOT (TB_MTT_VEICULO_id, status, tipo) VALUES (1, 1, 'GPS Tracker');
INSERT INTO TB_MTT_IOT (TB_MTT_VEICULO_id, status, tipo) VALUES (2, 0, 'Acelerômetro');

-- Logradouros
INSERT INTO TB_MTT_LOGRADOURO (TB_MTT_USUARIO_id, TB_MTT_FILIAL_id, TB_MTT_CIDADE_id, nome_logradouro, numero_logradouro, cep, complemento) 
VALUES ((SELECT id FROM TB_MTT_USUARIO WHERE username = 'user'), NULL, 1, 'Rua Augusta', '1250', '01304001', 'Apto 101');
INSERT INTO TB_MTT_LOGRADOURO (TB_MTT_USUARIO_id, TB_MTT_FILIAL_id, TB_MTT_CIDADE_id, nome_logradouro, numero_logradouro, cep, complemento) 
VALUES (NULL, 1, 3, 'Avenida Afonso Pena', '4000', '30130009', 'Loja 05');

INSERT INTO TB_MTT_FINANCAS (agencia, conta, saldo) VALUES (1, 1, 4363.55);
INSERT INTO TB_MTT_FINANCAS (agencia, conta, saldo) VALUES (1, 2, 4794.76);
INSERT INTO TB_MTT_FINANCAS (agencia, conta, saldo) VALUES (1, 3, 4718.25);
INSERT INTO TB_MTT_FINANCAS (agencia, conta, saldo) VALUES (1, 4, 5387.45);
INSERT INTO TB_MTT_FINANCAS (agencia, conta, saldo) VALUES (1, 5, 5027.34);
INSERT INTO TB_MTT_FINANCAS (agencia, conta, saldo) VALUES (2, 1, 5652.84);
INSERT INTO TB_MTT_FINANCAS (agencia, conta, saldo) VALUES (2, 2, 4583.02);
INSERT INTO TB_MTT_FINANCAS (agencia, conta, saldo) VALUES (2, 3, 5555.77);
INSERT INTO TB_MTT_FINANCAS (agencia, conta, saldo) VALUES (2, 4, 5936.67);
INSERT INTO TB_MTT_FINANCAS (agencia, conta, saldo) VALUES (2, 5, 4508.74);