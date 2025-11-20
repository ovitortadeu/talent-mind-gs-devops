package br.com.challenge_java.service;

import br.com.challenge_java.exception.BusinessException;
import br.com.challenge_java.exception.ResourceNotFoundException;
import br.com.challenge_java.model.Cidade;
import br.com.challenge_java.model.Logradouro;
import br.com.challenge_java.repository.CidadeRepository;
import br.com.challenge_java.repository.LogradouroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogradouroService {

    private final LogradouroRepository logradouroRepository;
    private final CidadeRepository cidadeRepository; 

    @Transactional(readOnly = true)
    public List<Logradouro> buscarTodos() {
        return logradouroRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Logradouro findEntityById(Long id) {
        return logradouroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Logradouro não encontrado com ID: " + id));
    }

    @Transactional
    public Logradouro salvar(Logradouro logradouro) {
        if (logradouro.getCidade() == null || logradouro.getCidade().getId() == null) {
            throw new BusinessException("A Cidade é obrigatória.");
        }
        
        Cidade cidade = cidadeRepository.findById(logradouro.getCidade().getId())
            .orElseThrow(() -> new ResourceNotFoundException("Cidade não encontrada."));
        
        logradouro.setCidade(cidade);
        
        logradouro.setFilial(null);
        logradouro.setUsuario(null);

        return logradouroRepository.save(logradouro);
    }

    @Transactional
    public Logradouro atualizar(Long id, Logradouro dadosDoFormulario) {
        Logradouro logradouroDoBanco = findEntityById(id);

        if (dadosDoFormulario.getCidade() == null || dadosDoFormulario.getCidade().getId() == null) {
            throw new BusinessException("A Cidade é obrigatória.");
        }
        
        Cidade cidade = cidadeRepository.findById(dadosDoFormulario.getCidade().getId())
            .orElseThrow(() -> new ResourceNotFoundException("Cidade não encontrada."));

        logradouroDoBanco.setNomeLogradouro(dadosDoFormulario.getNomeLogradouro());
        logradouroDoBanco.setNumeroLogradouro(dadosDoFormulario.getNumeroLogradouro());
        logradouroDoBanco.setCep(dadosDoFormulario.getCep());
        logradouroDoBanco.setComplemento(dadosDoFormulario.getComplemento());
        logradouroDoBanco.setCidade(cidade);

        return logradouroRepository.save(logradouroDoBanco);
    }

    @Transactional
    public void deletarLogradouro(Long id) {
        if (!logradouroRepository.existsById(id)) {
            throw new ResourceNotFoundException("Logradouro não encontrado com ID: " + id);
        }
        try {
            logradouroRepository.deleteById(id);
        } catch (Exception e) {
            throw new BusinessException("Não é possível excluir o logradouro. Pode estar em uso por um Pátio.");
        }
    }
}