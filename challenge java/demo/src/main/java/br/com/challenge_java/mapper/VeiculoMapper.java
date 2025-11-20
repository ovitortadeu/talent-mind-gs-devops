package br.com.challenge_java.mapper;

import br.com.challenge_java.dto.VeiculoCreateDTO;
import br.com.challenge_java.dto.VeiculoDTO;
import br.com.challenge_java.model.Patio; // <-- ALTERADO
import br.com.challenge_java.model.Veiculo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VeiculoMapper {

    @Mapping(source = "patio.id", target = "patioId") // <-- ALTERADO
    @Mapping(source = "patio.nome", target = "nomePatio") // <-- ALTERADO
    VeiculoDTO toVeiculoDTO(Veiculo veiculo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patio", source = "patioId", qualifiedByName = "patioFromId") // <-- ALTERADO
    Veiculo toVeiculo(VeiculoCreateDTO veiculoCreateDTO);

    @Named("patioFromId") // <-- ALTERADO
    default Patio patioFromId(Long id) { // <-- ALTERADO
        if (id == null) {
            return null;
        }
        Patio patio = new Patio(); // <-- ALTERADO
        patio.setId(id);
        return patio;
    }
}