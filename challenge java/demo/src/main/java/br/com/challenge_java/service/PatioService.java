package br.com.challenge_java.service;

import br.com.challenge_java.exception.BusinessException;
import br.com.challenge_java.exception.ResourceNotFoundException;
import br.com.challenge_java.model.Logradouro;
import br.com.challenge_java.model.Patio;
import br.com.challenge_java.repository.LogradouroRepository;
import br.com.challenge_java.repository.PatioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatioService {

    private final PatioRepository patioRepository;
    private final LogradouroRepository logradouroRepository;

    @Transactional(readOnly = true)
    public List<Patio> buscarTodosPatios() {
        return patioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Patio findEntityById(Long id) {
        return patioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pátio não encontrado com ID: " + id));
    }

    @Transactional
    public Patio salvar(Patio patio) {
        // Validação de Logradouro (essencial, pois é NOT NULL)
        if (patio.getLogradouro() == null || patio.getLogradouro().getId() == null) {
            throw new BusinessException("O Logradouro é obrigatório para salvar o pátio.");
        }
        
        Logradouro logradouro = logradouroRepository.findById(patio.getLogradouro().getId())
            .orElseThrow(() -> new ResourceNotFoundException("Logradouro não encontrado."));
        
        patio.setLogradouro(logradouro);
        return patioRepository.save(patio);
    }

    @Transactional
    public Patio atualizar(Long id, Patio dadosDoFormulario) {
        Patio patioDoBanco = findEntityById(id);

        if (dadosDoFormulario.getLogradouro() == null || dadosDoFormulario.getLogradouro().getId() == null) {
            throw new BusinessException("O Logradouro é obrigatório para atualizar o pátio.");
        }
        
        Logradouro logradouro = logradouroRepository.findById(dadosDoFormulario.getLogradouro().getId())
            .orElseThrow(() -> new ResourceNotFoundException("Logradouro não encontrado."));

        patioDoBanco.setNome(dadosDoFormulario.getNome());
        patioDoBanco.setLogradouro(logradouro);

        return patioRepository.save(patioDoBanco);
    }

    @Transactional
    public void deletarPatio(Long id) {
        if (!patioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pátio não encontrado com ID: " + id);
        }
        try {
            patioRepository.deleteById(id);
        } catch (Exception e) {
            // Captura erro se um veículo ainda estiver associado a este pátio
            throw new BusinessException("Não é possível excluir o pátio. Pode haver veículos associados a ele.");
        }
    }
}