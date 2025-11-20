-- 1. ESPECIFICAÇÃO DO PACOTE
CREATE OR REPLACE PACKAGE PKG_MOTTU_GERENCIAMENTO AS

    PROCEDURE PR_DADOS_VEICULO_PATIO_JSON(
        p_patio_id IN NUMBER,
        p_json_result OUT CLOB
    );
    
    FUNCTION FN_VALIDAR_SENHA(
        p_senha IN VARCHAR2
    ) RETURN BOOLEAN;

    PROCEDURE PR_RELATORIO_FINANCEIRO_MANUAL;

END PKG_MOTTU_GERENCIAMENTO;
/

-- 2. CORPO DO PACOTE (AGORA PREENCHIDO CORRETAMENTE)
CREATE OR REPLACE PACKAGE BODY PKG_MOTTU_GERENCIAMENTO AS

    -- Função interna de JSON (privada para o pacote)
    FUNCTION FN_REGISTRO_JSON_INTERNA(
        p_placa_nova TB_MTT_VEICULO.placa_nova%TYPE,
        p_tipo_veiculo TB_MTT_VEICULO.tipo_veiculo%TYPE,
        p_nome_patio VARCHAR2
    ) RETURN VARCHAR2
    IS
    BEGIN
        -- Lógica simples de concatenação
        RETURN '{"placa": "' || p_placa_nova || '", "tipo": "' || p_tipo_veiculo || '", "patio": "' || p_nome_patio || '"}';
    EXCEPTION
        WHEN OTHERS THEN
            RETURN '{"erro": "Erro ao gerar JSON interno."}';
    END FN_REGISTRO_JSON_INTERNA;


    -- Implementação da Procedure pública (corrigida para o novo modelo)
    PROCEDURE PR_DADOS_VEICULO_PATIO_JSON(
        p_patio_id IN NUMBER,
        p_json_result OUT CLOB
    )
    IS
        CURSOR c_veiculo_patio IS
            SELECT v.placa_nova, v.tipo_veiculo, p.nome_patio
            FROM TB_MTT_VEICULO v
            JOIN TB_MTT_PATIO p ON v.TB_MTT_PATIO_id = p.id
            WHERE p.id = p_patio_id;
            
        v_json_string CLOB := '[';
        v_contador NUMBER := 0;
    BEGIN
        FOR rec IN c_veiculo_patio LOOP
            IF v_contador > 0 THEN 
                v_json_string := v_json_string || ', '; 
            END IF;
            
            v_json_string := v_json_string || FN_REGISTRO_JSON_INTERNA(rec.placa_nova, rec.tipo_veiculo, rec.nome_patio);
            v_contador := v_contador + 1;
        END LOOP;
    
        IF v_contador = 0 THEN 
            RAISE NO_DATA_FOUND; 
        END IF;
        
        v_json_string := v_json_string || ']';
        p_json_result := v_json_string;
        
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_json_result := '{"erro": "Nenhum veículo encontrado para este pátio."}';
        WHEN OTHERS THEN
            p_json_result := '{"erro": "Erro inesperado na procedure: ' || SQLERRM || '"}';
    END PR_DADOS_VEICULO_PATIO_JSON;


    -- Implementação da Função pública (mesma da S3)
    FUNCTION FN_VALIDAR_SENHA(p_senha IN VARCHAR2) RETURN BOOLEAN
    IS
        e_senha_nula EXCEPTION;
        e_senha_curta EXCEPTION;
    BEGIN
        IF p_senha IS NULL THEN RAISE e_senha_nula; END IF;
        IF LENGTH(p_senha) < 8 THEN RAISE e_senha_curta; END IF;
        RETURN REGEXP_LIKE(p_senha, '[A-Z]') AND REGEXP_LIKE(p_senha, '[a-z]') AND REGEXP_LIKE(p_senha, '[0-9]');
    EXCEPTION
        WHEN e_senha_nula THEN
            DBMS_OUTPUT.PUT_LINE('Erro de Validação: A senha não pode ser nula.');
            RETURN FALSE;
        WHEN e_senha_curta THEN
            DBMS_OUTPUT.PUT_LINE('Erro de Validação: A senha deve ter no mínimo 8 caracteres.');
            RETURN FALSE;
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Erro inesperado na função FN_VALIDAR_SENHA: ' || SQLERRM);
            RETURN FALSE;
    END FN_VALIDAR_SENHA;
    

    -- Implementação da Procedure pública (mesma da S3)
    PROCEDURE PR_RELATORIO_FINANCEIRO_MANUAL
    IS
        CURSOR c_financas IS SELECT agencia, conta, saldo FROM TB_MTT_FINANCAS ORDER BY agencia, conta;
        v_agencia_anterior NUMBER := -1;
        v_subtotal NUMBER(12, 2) := 0;
        v_total_geral NUMBER(14, 2) := 0;
        v_contador_registros NUMBER := 0;
        v_contador_grupo NUMBER := 0;
        e_tabela_vazia EXCEPTION;
    BEGIN
        DBMS_OUTPUT.PUT_LINE('Agencia   Conta       Saldo');
        DBMS_OUTPUT.PUT_LINE('-------   -----       --------');
    
        FOR rec IN c_financas LOOP
            v_contador_registros := v_contador_registros + 1;
            IF v_agencia_anterior <> rec.agencia AND v_agencia_anterior <> -1 THEN
                DBMS_OUTPUT.PUT_LINE('Sub Total         ' || TO_CHAR(v_subtotal, '99999.99'));
                v_subtotal := 0;
                v_contador_grupo := 0;
            END IF;
            DBMS_OUTPUT.PUT_LINE(RPAD(rec.agencia, 11) || RPAD(rec.conta, 11) || TO_CHAR(rec.saldo, '99999.99'));
            v_subtotal := v_subtotal + rec.saldo;
            v_total_geral := v_total_geral + rec.saldo;
            v_contador_grupo := v_contador_grupo + 1;
            v_agencia_anterior := rec.agencia;
        END LOOP;
    
        IF v_contador_registros = 0 THEN RAISE e_tabela_vazia; END IF;
        
        DBMS_OUTPUT.PUT_LINE('Sub Total         ' || TO_CHAR(v_subtotal, '99999.99'));
        DBMS_OUTPUT.PUT_LINE('Total Geral       ' || TO_CHAR(v_total_geral, '999999.99'));
    EXCEPTION
        WHEN e_tabela_vazia THEN
            DBMS_OUTPUT.PUT_LINE('ERRO: A tabela de finanças está vazia.');
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('ERRO Inesperado: ' || SQLCODE || ' - ' || SQLERRM);
    END PR_RELATORIO_FINANCEIRO_MANUAL;

END PKG_MOTTU_GERENCIAMENTO;
/