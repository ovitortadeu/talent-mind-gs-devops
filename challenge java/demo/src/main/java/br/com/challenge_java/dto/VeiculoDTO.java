package br.com.challenge_java.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoDTO {
    private Long id;
    private Long patioId; // <-- ALTERADO
    private String nomePatio; // <-- ALTERADO
    private String placaAntiga;
    private String placaNova;
    private String tipoVeiculo;
}