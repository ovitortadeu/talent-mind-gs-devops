package br.com.challenge_java.service;

import br.com.challenge_java.dto.VeiculoCreateDTO;
import br.com.challenge_java.dto.VeiculoDTO;
import br.com.challenge_java.exception.BusinessException;
import br.com.challenge_java.exception.ResourceNotFoundException;
import br.com.challenge_java.mapper.VeiculoMapper;
import br.com.challenge_java.model.Locacao; // <-- ADICIONADO
import br.com.challenge_java.model.Patio;
import br.com.challenge_java.model.Usuario;
import br.com.challenge_java.model.Veiculo;
import br.com.challenge_java.repository.LocacaoRepository; // <-- ADICIONADO
import br.com.challenge_java.repository.PatioRepository;
import br.com.challenge_java.repository.UsuarioRepository;
import br.com.challenge_java.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final UsuarioRepository usuarioRepository;
    private final VeiculoMapper veiculoMapper;
    private final PatioRepository patioRepository;
    private final LocacaoRepository locacaoRepository; // <-- ADICIONADO

    // --- MÉTODOS DE API (Precisam ser refatorados para Pátio) ---

    // ... (métodos criarVeiculo e atualizarVeiculo permanecem iguais) ...

    @Transactional
    public VeiculoDTO criarVeiculo(VeiculoCreateDTO veiculoCreateDTO) {
        // Validação de Placa (para API)
        if (veiculoRepository.findByPlacaNova(veiculoCreateDTO.getPlacaNova()).isPresent()) {
            throw new BusinessException("Já existe um veículo cadastrado com a placa: " + veiculoCreateDTO.getPlacaNova());
        }
        
        Patio patio = patioRepository.findById(veiculoCreateDTO.getPatioId()) // Assumindo que DTO foi corrigido
                .orElseThrow(() -> new ResourceNotFoundException("Pátio não encontrado com ID: " + veiculoCreateDTO.getPatioId()));

        Veiculo veiculo = veiculoMapper.toVeiculo(veiculoCreateDTO);
        veiculo.setPatio(patio);
        Veiculo veiculoSalvo = veiculoRepository.save(veiculo);
        return veiculoMapper.toVeiculoDTO(veiculoSalvo);
    }

    @Transactional
    public VeiculoDTO atualizarVeiculo(Long id, VeiculoCreateDTO veiculoCreateDTO) {
        Veiculo veiculoExistente = findEntityById(id);

        // Validação de Placa (para API)
        Optional<Veiculo> veiculoDuplicado = veiculoRepository.findByPlacaNova(veiculoCreateDTO.getPlacaNova());
        if (veiculoDuplicado.isPresent() && !veiculoDuplicado.get().getId().equals(id)) {
            throw new BusinessException("A placa " + veiculoCreateDTO.getPlacaNova() + " já pertence a outro veículo.");
        }
        
        Patio patio = patioRepository.findById(veiculoCreateDTO.getPatioId()) // Assumindo que DTO foi corrigido
                .orElseThrow(() -> new ResourceNotFoundException("Pátio não encontrado com ID: " + veiculoCreateDTO.getPatioId()));

        veiculoExistente.setPatio(patio);
        veiculoExistente.setPlacaAntiga(veiculoCreateDTO.getPlacaAntiga());
        veiculoExistente.setPlacaNova(veiculoCreateDTO.getPlacaNova());
        veiculoExistente.setTipoVeiculo(veiculoCreateDTO.getTipoVeiculo());
        Veiculo veiculoAtualizado = veiculoRepository.save(veiculoExistente);
        return veiculoMapper.toVeiculoDTO(veiculoAtualizado);
    }

    // --- NOVO MÉTODO PARA API ---
    @Transactional(readOnly = true)
    public List<VeiculoDTO> listarPorLocacaoUsuarioId(Long usuarioId) {
        List<Locacao> locacoes = locacaoRepository.findByUsuarioId(usuarioId);
        return locacoes.stream()
                .map(Locacao::getVeiculo) // Extrai o Veiculo de cada Locacao
                .distinct() // Evita duplicatas se um usuário alugou o mesmo veículo várias vezes
                .map(veiculoMapper::toVeiculoDTO) // Mapeia Veiculo para VeiculoDTO
                .collect(Collectors.toList());
    }
    // ----------------------------


    // --- MÉTODOS DO WEB CONTROLLER (Corrigidos) ---

    @Transactional(readOnly = true)
    public VeiculoDTO buscarPorId(Long id) {
        return veiculoRepository.findById(id)
                .map(veiculoMapper::toVeiculoDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado com ID: " + id));
    }
    
    // ... (outros métodos do Web Controller permanecem iguais) ...
    @Transactional(readOnly = true)
    public Page<VeiculoDTO> listarTodosPaginado(Pageable pageable) {
        return veiculoRepository.findAll(pageable)
                .map(veiculoMapper::toVeiculoDTO);
    }

    @Transactional(readOnly = true)
    public List<Veiculo> buscarTodosVeiculos() {
        return veiculoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Veiculo findEntityById(Long id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado com ID: " + id));
    }
    
    @Transactional
    public Veiculo salvar(Veiculo veiculo) {
        // ADICIONADO DE VOLTA: Validação de placa duplicada
        if (veiculoRepository.findByPlacaNova(veiculo.getPlacaNova()).isPresent()) {
            throw new BusinessException("Já existe um veículo cadastrado com a placa: " + veiculo.getPlacaNova());
        }
        
        if (veiculo.getPatio() != null && veiculo.getPatio().getId() != null) {
            Patio patio = patioRepository.findById(veiculo.getPatio().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pátio não encontrado para o veículo."));
            veiculo.setPatio(patio);
        } else {
             throw new BusinessException("O Pátio é obrigatório para salvar o veículo.");
        }
        return veiculoRepository.save(veiculo);
    }

    @Transactional
    public Veiculo atualizar(Long id, Veiculo dadosDoFormulario) {
        Veiculo veiculoDoBanco = findEntityById(id);

        // ADICIONADO DE VOLTA: Validação de placa duplicada
        Optional<Veiculo> veiculoDuplicado = veiculoRepository.findByPlacaNova(dadosDoFormulario.getPlacaNova());
        if (veiculoDuplicado.isPresent() && !veiculoDuplicado.get().getId().equals(id)) {
            throw new BusinessException("A placa " + dadosDoFormulario.getPlacaNova() + " já pertence a outro veículo.");
        }
        
        veiculoDoBanco.setPlacaNova(dadosDoFormulario.getPlacaNova());
        veiculoDoBanco.setPlacaAntiga(dadosDoFormulario.getPlacaAntiga());
        veiculoDoBanco.setTipoVeiculo(dadosDoFormulario.getTipoVeiculo());
        
        if (dadosDoFormulario.getPatio() != null && dadosDoFormulario.getPatio().getId() != null) {
            Patio patio = patioRepository.findById(dadosDoFormulario.getPatio().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pátio não encontrado para atualização do veículo."));
            veiculoDoBanco.setPatio(patio);
        } else {
             throw new BusinessException("O Pátio é obrigatório para atualizar o veículo.");
        }

        return veiculoRepository.save(veiculoDoBanco);
    }

    @Transactional
    public void deletarVeiculo(Long id) {
        if (!veiculoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Veículo não encontrado com ID: " + id);
        }
        veiculoRepository.deleteById(id);
    }
}