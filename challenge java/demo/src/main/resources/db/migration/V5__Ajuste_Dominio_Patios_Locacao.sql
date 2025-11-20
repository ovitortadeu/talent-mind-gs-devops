-- ======================================================================
-- SCRIPT V5 - CORRIGIDO PARA LIDAR COM TRIGGERS
-- ======================================================================

-- 0. Desabilitar o trigger que está causando o conflito
--    (Usamos um bloco anônimo para ignorar o erro caso ele não exista)
BEGIN
   EXECUTE IMMEDIATE 'ALTER TRIGGER TRG_AUDITORIA_VEICULO DISABLE';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE = -4080 THEN
         NULL; -- Trigger não existe, ignora
      ELSE
         RAISE;
      END IF;
END;
/

-- 1. Criar tabela de Pátios
CREATE TABLE TB_MTT_PATIO (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome_patio VARCHAR2(100) NOT NULL,
    TB_MTT_LOGRADOURO_id NUMBER NOT NULL UNIQUE,
    CONSTRAINT TB_MTT_PATIO_LOGRADOURO_FK FOREIGN KEY (TB_MTT_LOGRADOURO_id) REFERENCES TB_MTT_LOGRADOURO (id)
);

-- 2. Criar tabela de Locações
CREATE TABLE TB_MTT_LOCACAO (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    TB_MTT_USUARIO_id NUMBER NOT NULL,
    TB_MTT_VEICULO_id NUMBER NOT NULL,
    dt_inicio TIMESTAMP NOT NULL,
    dt_fim_prevista TIMESTAMP NOT NULL,
    dt_fim_real TIMESTAMP NULL,
    status_locacao VARCHAR2(20),
    valor_total NUMBER(10, 2),
    CONSTRAINT TB_MTT_LOCACAO_USUARIO_FK FOREIGN KEY (TB_MTT_USUARIO_id) REFERENCES TB_MTT_USUARIO (id),
    CONSTRAINT TB_MTT_LOCACAO_VEICULO_FK FOREIGN KEY (TB_MTT_VEICULO_id) REFERENCES TB_MTT_VEICULO (id)
);

-- 3. Inserir um Pátio Padrão (necessário para os veículos existentes)
--    Usará o Logradouro da Filial 1, que já existe do script V4
INSERT INTO TB_MTT_PATIO (nome_patio, TB_MTT_LOGRADOURO_id)
SELECT 'Pátio Central', id FROM TB_MTT_LOGRADOURO WHERE TB_MTT_FILIAL_id = 1 AND ROWNUM = 1;

-- 4. Adicionar a coluna de Pátio no Veículo (permitindo nulos por enquanto)
ALTER TABLE TB_MTT_VEICULO ADD TB_MTT_PATIO_id NUMBER;

-- 5. ATUALIZAR todos os veículos existentes para pertencerem ao Pátio Padrão
--    (Isso não vai mais disparar o trigger, pois ele está DESABILITADO)
UPDATE TB_MTT_VEICULO
SET TB_MTT_PATIO_id = (SELECT id FROM TB_MTT_PATIO WHERE nome_patio = 'Pátio Central')
WHERE TB_MTT_PATIO_id IS NULL;

-- 6. AGORA SIM, podemos adicionar a restrição NOT NULL
ALTER TABLE TB_MTT_VEICULO MODIFY TB_MTT_PATIO_id NUMBER NOT NULL;

-- 7. Remover a FK de Usuário da tabela Veículo
BEGIN
   EXECUTE IMMEDIATE 'ALTER TABLE TB_MTT_VEICULO DROP CONSTRAINT TB_MTT_VEICULO_USUARIO_FK';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE = -2443 THEN NULL; ELSE RAISE; END IF;
END;
/

-- 8. Dropar a coluna de Usuário
ALTER TABLE TB_MTT_VEICULO DROP COLUMN TB_MTT_USUARIO_id;

-- 9. Adicionar a nova FK de Pátio
ALTER TABLE TB_MTT_VEICULO ADD CONSTRAINT TB_MTT_VEICULO_PATIO_FK FOREIGN KEY (TB_MTT_PATIO_id) REFERENCES TB_MTT_PATIO (id);

-- 10. Remover o índice único de usuário do logradouro
BEGIN
   EXECUTE IMMEDIATE 'ALTER TABLE TB_MTT_LOGRADOURO DROP CONSTRAINT TB_MTT_LOGRADOURO_USUARIO_UIDX';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE = -2443 THEN NULL; ELSE RAISE; END IF;
END;
/
BEGIN
   EXECUTE IMMEDIATE 'DROP INDEX TB_MTT_LOGRADOURO_USUARIO_UIDX';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE = -1418 THEN NULL; ELSE RAISE; END IF;
END;
/

-- 10.5. Remover a restrição de verificação (CHECK) antiga
BEGIN
   EXECUTE IMMEDIATE 'ALTER TABLE TB_MTT_LOGRADOURO DROP CONSTRAINT CHK_LOGRADOURO_OWNER';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE = -2443 THEN NULL; ELSE RAISE; END IF;
END;
/

-- 11. Recriar o Trigger de Auditoria (CORRIGIDO para o novo modelo)
CREATE OR REPLACE TRIGGER TRG_AUDITORIA_VEICULO
AFTER INSERT OR UPDATE OR DELETE ON TB_MTT_VEICULO
FOR EACH ROW
DECLARE
    v_operacao VARCHAR2(10);
    v_dados_antigos CLOB;
    v_dados_novos CLOB;
BEGIN
    IF INSERTING THEN v_operacao := 'INSERT';
    ELSIF UPDATING THEN v_operacao := 'UPDATE';
    ELSE v_operacao := 'DELETE';
    END IF;

    -- Corrigido: Referencia :OLD.TB_MTT_PATIO_id
    IF DELETING OR UPDATING THEN
        v_dados_antigos := 'ID: ' || :OLD.id || ', PatioID: ' || :OLD.TB_MTT_PATIO_id || ', Placa: ' || :OLD.placa_nova || ', Tipo: ' || :OLD.tipo_veiculo;
    END IF;
    
    -- Corrigido: Referencia :NEW.TB_MTT_PATIO_id
    IF INSERTING OR UPDATING THEN
        v_dados_novos := 'ID: ' || :NEW.id || ', PatioID: ' || :NEW.TB_MTT_PATIO_id || ', Placa: ' || :NEW.placa_nova || ', Tipo: ' || :NEW.tipo_veiculo;
    END IF;

    INSERT INTO TB_AUDITORIA_VEICULO (nome_usuario, tipo_operacao, data_hora, dados_antigos, dados_novos)
    VALUES (USER, v_operacao, SYSTIMESTAMP, v_dados_antigos, v_dados_novos);
EXCEPTION
    WHEN OTHERS THEN DBMS_OUTPUT.PUT_LINE('ERRO na Trigger de Auditoria: ' || SQLERRM);
END TRG_AUDITORIA_VEICULO;
/

-- 12. Reabilitar o trigger (não é estritamente necessário, mas é uma boa prática)
ALTER TRIGGER TRG_AUDITORIA_VEICULO ENABLE;