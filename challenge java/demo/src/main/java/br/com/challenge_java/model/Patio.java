package br.com.challenge_java.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.List;

@Entity
@Table(name = "TB_MTT_PATIO")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Patio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "nome_patio", length = 100, nullable = false)
    private String nome;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TB_MTT_LOGRADOURO_id", nullable = false, unique = true)
    private Logradouro logradouro;

    @ToString.Exclude
    @OneToMany(mappedBy = "patio", cascade = CascadeType.ALL)
    private List<Veiculo> veiculos;
}