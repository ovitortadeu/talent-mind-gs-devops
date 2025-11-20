package br.com.challenge_java.control;

import br.com.challenge_java.exception.BusinessException;
import br.com.challenge_java.model.Patio;
import br.com.challenge_java.repository.LogradouroRepository;
import br.com.challenge_java.service.PatioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/patios")
@RequiredArgsConstructor
public class PatioWebController {

    private final PatioService patioService;
    private final LogradouroRepository logradouroRepository;

    @GetMapping
    public String listarPatios(Model model) {
        model.addAttribute("patios", patioService.buscarTodosPatios());
        return "patios/list"; 
    }

    @GetMapping("/new")
    public String exibirFormularioNovo(Model model) {
        model.addAttribute("patio", new Patio());
        model.addAttribute("todosLogradouros", logradouroRepository.findAll());
        return "patios/form";
    }

    @PostMapping("/save")
    public String salvarPatio(@Valid @ModelAttribute("patio") Patio patio,
                              BindingResult result,
                              Model model, 
                              RedirectAttributes attributes) {
        
        if (patio.getLogradouro() == null || patio.getLogradouro().getId() == null) {
            result.rejectValue("logradouro", "NotEmpty", "O logradouro é obrigatório.");
        }

        if (result.hasErrors()) {
            model.addAttribute("todosLogradouros", logradouroRepository.findAll());
            return "patios/form";
        }

        try {
            if (patio.getId() == null) {
                patioService.salvar(patio);
                attributes.addFlashAttribute("message", "Pátio criado com sucesso!");
            } else {
                patioService.atualizar(patio.getId(), patio);
                attributes.addFlashAttribute("message", "Pátio de ID " + patio.getId() + " atualizado com sucesso!");
            }
        } catch (BusinessException e) {
            model.addAttribute("todosLogradouros", logradouroRepository.findAll());
            result.rejectValue("nome", "Error", e.getMessage());
            return "patios/form";
        }
        
        return "redirect:/patios";
    }

    @GetMapping("/edit/{id}")
    public String exibirFormularioEdicao(@PathVariable Long id, Model model) {
        model.addAttribute("patio", patioService.findEntityById(id));
        model.addAttribute("todosLogradouros", logradouroRepository.findAll());
        return "patios/form";
    }

    @GetMapping("/delete/{id}")
    public String deletarPatio(@PathVariable Long id, RedirectAttributes attributes) {
        try {
            patioService.deletarPatio(id);
            attributes.addFlashAttribute("message", "Pátio de ID " + id + " deletado com sucesso!");
        } catch (BusinessException e) {
            attributes.addFlashAttribute("error", "Erro ao deletar pátio: " + e.getMessage());
        }
        return "redirect:/patios";
    }
}