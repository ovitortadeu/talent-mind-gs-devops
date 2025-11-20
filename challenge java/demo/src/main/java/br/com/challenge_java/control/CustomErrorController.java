package br.com.challenge_java.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController {

    @GetMapping("/acesso-negado")
    public String acessoNegado() {
        return "error/403"; // Aponta para 'templates/error/403.html'
    }
    
    // Você pode adicionar outros handlers de erro aqui se necessário
}