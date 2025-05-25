package br.com.fiap.mottutrackapi1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "PATIOS")
public class Patio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PATIO")
    private Long id;

    @NotNull(message = "Filial é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FILIAL", nullable = false, foreignKey = @ForeignKey(name = "FK_PATIOS_FILIAL"))
    private Filial filial;

    @NotBlank(message = "Nome do pátio é obrigatório")
    @Size(max = 100, message = "Nome do pátio deve ter no máximo 100 caracteres")
    @Column(name = "NOME_PATIO", nullable = false, length = 100)
    private String nomePatio;

    @NotBlank(message = "Descrição do pátio é obrigatória")
    @Size(max = 500, message = "Descrição do pátio deve ter no máximo 500 caracteres")
    @Column(name = "DESCRICAO_PATIO", nullable = false, length = 500)
    private String descricaoPatio;

    @Column(name = "CAPACIDADE_TOTAL_MOTOS")
    private Integer capacidadeTotalMotos; // NUMBER(10) maps well to Integer

    @Column(name = "DIMENSOES_PATIO_METROS_QUADRADOS", precision = 18, scale = 2)
    private BigDecimal dimensoesPatioMetrosQuadrados; // DECIMAL(18, 2) maps to BigDecimal

    @Size(max = 500, message = "URL da imagem do layout deve ter no máximo 500 caracteres")
    @Column(name = "LAYOUT_PATIO_IMG_URL", length = 500)
    private String layoutPatioImgUrl;

    @CreationTimestamp
    @Column(name = "DATA_CADASTRO", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    // Lombok @Data generates getters, setters, toString, equals, hashCode
}

