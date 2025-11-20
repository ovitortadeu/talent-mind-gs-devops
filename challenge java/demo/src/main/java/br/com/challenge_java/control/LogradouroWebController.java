package br.com.challenge_java.control;

import br.com.challenge_java.exception.BusinessException;
import br.com.challenge_java.model.Logradouro;
import br.com.challenge_java.repository.CidadeRepository;
import br.com.challenge_java.service.LogradouroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;

@Controller
@RequestMapping("/logradouros")
@RequiredArgsConstructor
public class LogradouroWebController {

    private final LogradouroService logradouroService;
    private final CidadeRepository cidadeRepository;

    @GetMapping
    public String listarLogradouros(Model model) {
        model.addAttribute("logradouros", logradouroService.buscarTodos());
        return "logradouros/list"; // -> templates/logradouros/list.html
    }

    @GetMapping("/new")
    public String exibirFormularioNovo(Model model) {
        model.addAttribute("logradouro", new Logradouro());
        model.addAttribute("todasCidades", cidadeRepository.findAll()); 
        return "logradouros/form"; // -> templates/logradouros/form.html
    }

    @PostMapping("/save")
    public String salvarLogradouro(@Valid @ModelAttribute("logradouro") Logradouro logradouro,
                                   BindingResult result,
                                   Model model, 
                                   RedirectAttributes attributes) {

        if (logradouro.getCidade() == null || logradouro.getCidade().getId() == null) {
            result.rejectValue("cidade", "NotEmpty", "A cidade é obrigatória.");
        }

        if (result.hasErrors()) {
            model.addAttribute("todasCidades", cidadeRepository.findAll()); 
            return "logradouros/form";
        }

        try {
            if (logradouro.getId() == null) {
                logradouroService.salvar(logradouro);
                attributes.addFlashAttribute("message", "Endereço (Logradouro) criado com sucesso!");
            } else {
                logradouroService.atualizar(logradouro.getId(), logradouro);
                attributes.addFlashAttribute("message", "Endereço de ID " + logradouro.getId() + " atualizado com sucesso!");
            }
        } catch (BusinessException | DataIntegrityViolationException e) {
            model.addAttribute("todasCidades", cidadeRepository.findAll());
            result.rejectValue("nomeLogradouro", "Error", "Erro de banco de dados: " + e.getMessage());
            return "logradouros/form";
        }

        return "redirect:/logradouros";
    }
    @GetMapping("/edit/{id}")
    public String exibirFormularioEdicao(@PathVariable Long id, Model model) {
        model.addAttribute("logradouro", logradouroService.findEntityById(id));
        model.addAttribute("todasCidades", cidadeRepository.findAll()); 
        return "logradouros/form";
    }

    @GetMapping("/delete/{id}")
    public String deletarLogradouro(@PathVariable Long id, RedirectAttributes attributes) {
        try {
            logradouroService.deletarLogradouro(id);
            attributes.addFlashAttribute("message", "Endereço de ID " + id + " deletado com sucesso!");
        } catch (BusinessException e) {
            attributes.addFlashAttribute("error", "Erro ao deletar endereço: " + e.getMessage());
        }
        return "redirect:/logradouros";
    }
}