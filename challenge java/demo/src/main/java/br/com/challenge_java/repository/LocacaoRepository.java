package br.com.challenge_java.repository;

import br.com.challenge_java.model.Locacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // <-- ADICIONADO

@Repository
public interface LocacaoRepository extends JpaRepository<Locacao, Long> {
    // <-- ADICIONADO
    List<Locacao> findByUsuarioId(Long usuarioId);
}