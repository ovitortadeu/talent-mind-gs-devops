package br.com.challenge_java.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RelatorioService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public String gerarRelatorioVeiculosPorPatio(Long patioId) {
        
        // Chama a procedure DENTRO do pacote
        StoredProcedureQuery query = entityManager
            .createStoredProcedureQuery("PKG_MOTTU_GERENCIAMENTO.PR_DADOS_VEICULO_PATIO_JSON");
            
        // Registrar parâmetros de entrada (IN)
        query.registerStoredProcedureParameter("p_patio_id", Long.class, jakarta.persistence.ParameterMode.IN);
        query.setParameter("p_patio_id", patioId);

        // Registrar parâmetros de saída (OUT)
        query.registerStoredProcedureParameter("p_json_result", String.class, jakarta.persistence.ParameterMode.OUT);

        // Executar
        query.execute();

        // Obter resultado do parâmetro de saída
        String jsonResult = (String) query.getOutputParameterValue("p_json_result");

        return jsonResult;
    }
}