package br.com.challenge_java.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.math.BigDecimal;  

@Entity
@Table(name = "TB_MTT_LOCACAO")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Locacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TB_MTT_USUARIO_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TB_MTT_VEICULO_id", nullable = false)
    private Veiculo veiculo;

    @Column(name = "dt_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "dt_fim_prevista", nullable = false)
    private LocalDateTime dataFimPrevista;

    @Column(name = "dt_fim_real")
    private LocalDateTime dataFimReal;

    @Column(name = "status_locacao", length = 20)
    private String status;

    // 2. ALTERAR DE 'Double' PARA 'BigDecimal'
    @Column(name = "valor_total")
    private BigDecimal valorTotal; 
}