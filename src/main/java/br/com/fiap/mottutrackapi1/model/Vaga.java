package br.com.fiap.mottutrackapi1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "VAGAS", uniqueConstraints = {
    // Adjusted constraint to use ID_PATIO as AreaPatio is removed
    @UniqueConstraint(columnNames = {"ID_PATIO", "CODIGO_VAGA"}, name = "UK_VAGAS_CODIGO_PATIO") 
})
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_VAGA")
    private Long id;

    // Vaga now belongs directly to a Patio
    @NotNull(message = "Pátio é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PATIO", nullable = false, foreignKey = @ForeignKey(name = "FK_VAGAS_PATIO")) // Assuming FK name convention
    private Patio patio;

    @NotBlank(message = "Código da vaga é obrigatório")
    @Size(max = 20, message = "Código da vaga deve ter no máximo 20 caracteres")
    @Column(name = "CODIGO_VAGA", nullable = false, length = 20)
    private String codigoVaga;

    @NotBlank(message = "Tipo da vaga é obrigatório")
    @Size(max = 50, message = "Tipo da vaga deve ter no máximo 50 caracteres")
    @Column(name = "TIPO_VAGA", nullable = false, length = 50)
    private String tipoVaga;

    @NotBlank(message = "Status de ocupação é obrigatório")
    @Size(max = 20, message = "Status de ocupação deve ter no máximo 20 caracteres")
    @Column(name = "STATUS_OCUPACAO", nullable = false, length = 20)
    private String statusOcupacao = "LIVRE"; // Default value set in Java

    @Column(name = "COORDENADAS_VAGA_X", precision = 10, scale = 2)
    private BigDecimal coordenadasVagaX;

    @Column(name = "COORDENADAS_VAGA_Y", precision = 10, scale = 2)
    private BigDecimal coordenadasVagaY;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MOTO_OCUPANTE") // FK constraint might be added later or managed by JPA
    private Moto motoOcupante;

    @Column(name = "TIMESTAMP_ULTIMA_OCUPACAO")
    private LocalDateTime timestampUltimaOcupacao;

    // Lombok @Data generates getters, setters, toString, equals, hashCode
}

