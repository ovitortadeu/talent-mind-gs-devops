package br.com.challenge_java.control;

import br.com.challenge_java.exception.BusinessException; // NOVO IMPORT
import br.com.challenge_java.model.Veiculo;
import br.com.challenge_java.repository.PatioRepository;
import br.com.challenge_java.service.VeiculoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException; // NOVO IMPORT
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/veiculos")
@RequiredArgsConstructor
public class VeiculoWebController {

    private final VeiculoService veiculoService;
    private final PatioRepository patioRepository; 

    @GetMapping
    public String listarVeiculos(Model model) {
        model.addAttribute("veiculos", veiculoService.buscarTodosVeiculos());
        return "veiculos/list";
    }

    @GetMapping("/new")
    public String exibirFormularioNovo(Model model) {
        model.addAttribute("veiculo", new Veiculo());
        model.addAttribute("todosPatios", patioRepository.findAll());
        return "veiculos/form";
    }

    @PostMapping("/save")
    public String salvarVeiculo(@Valid @ModelAttribute("veiculo") Veiculo veiculo,
                              BindingResult result,
                              Model model, 
                              RedirectAttributes attributes) {
        
        if (veiculo.getPatio() == null || veiculo.getPatio().getId() == null) {
            result.rejectValue("patio", "NotEmpty", "O pátio é obrigatório.");
        }

        if (result.hasErrors()) {
            model.addAttribute("todosPatios", patioRepository.findAll());
            return "veiculos/form";
        }

        try {
            if (veiculo.getId() == null) {
                veiculoService.salvar(veiculo);
                attributes.addFlashAttribute("message", "Veículo criado com sucesso!");
            } else {
                veiculoService.atualizar(veiculo.getId(), veiculo);
                attributes.addFlashAttribute("message", "Veículo de ID " + veiculo.getId() + " atualizado com sucesso!");
            }
        } catch (BusinessException | DataIntegrityViolationException e) {
            // AQUI ESTÁ A CORREÇÃO:
            // Capturamos a exceção de negócio (placa duplicada)
            // ou qualquer outra exceção de integridade (do banco)
            
            // 1. Repopulamos o dropdown de pátios, que é perdido ao retornar
            model.addAttribute("todosPatios", patioRepository.findAll());
            
            // 2. Adicionamos a mensagem de erro para ser exibida no campo "placaNova"
            result.rejectValue("placaNova", "Duplicate", e.getMessage());
            
            // 3. Devolvemos o usuário para a página de formulário, mantendo seus dados
            return "veiculos/form";
        }
        
        return "redirect:/veiculos";
    }

    @GetMapping("/edit/{id}")
    public String exibirFormularioEdicao(@PathVariable Long id, Model model) {
        model.addAttribute("veiculo", veiculoService.findEntityById(id));
        model.addAttribute("todosPatios", patioRepository.findAll());
        return "veiculos/form";
    }

    @GetMapping("/delete/{id}")
    public String deletarVeiculo(@PathVariable Long id, RedirectAttributes attributes) {
        try {
            veiculoService.deletarVeiculo(id);
            attributes.addFlashAttribute("message", "Veículo de ID " + id + " deletado com sucesso!");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Erro ao deletar veículo: " + e.getMessage());
        }
        return "redirect:/veiculos";
    }
}