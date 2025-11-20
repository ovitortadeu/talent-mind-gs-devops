-- 1. Cria a tabela de auditoria que estava faltando
CREATE TABLE TB_AUDITORIA_VEICULO (
    id_auditoria NUMBER GENERATED ALWAYS AS IDENTITY,
    nome_usuario VARCHAR2(100),
    tipo_operacao VARCHAR2(10),
    data_hora TIMESTAMP,
    dados_antigos CLOB,
    dados_novos CLOB,
    CONSTRAINT pk_auditoria_veiculo PRIMARY KEY (id_auditoria)
);

-- 2. Recompila o trigger (que já existe, mas está inválido)
--    para que ele "enxergue" a nova tabela e se torne VÁLIDO.
ALTER TRIGGER TRG_AUDITORIA_VEICULO COMPILE;