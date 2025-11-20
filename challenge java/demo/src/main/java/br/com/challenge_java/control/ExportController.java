package br.com.challenge_java.control;

import br.com.challenge_java.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.io.IOException;

@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
public class ExportController {

    private final RelatorioService relatorioService;

    @GetMapping("/patio/{id}")
    public ResponseEntity<String> exportarVeiculosPorPatio(@PathVariable Long id) {
        
        String jsonString = relatorioService.gerarRelatorioVeiculosPorPatio(id);

        String nomeArquivo = "veiculos_por_patio.json";

        try (FileWriter fileWriter = new FileWriter(nomeArquivo)) {
            fileWriter.write(jsonString);
            System.out.println("Arquivo JSON '" + nomeArquivo + "' foi criado com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao salvar o arquivo JSON: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Erro ao salvar arquivo: " + e.getMessage());
        }

        return ResponseEntity.ok("Arquivo " + nomeArquivo + " gerado com sucesso na raiz do projeto.");
    }
}