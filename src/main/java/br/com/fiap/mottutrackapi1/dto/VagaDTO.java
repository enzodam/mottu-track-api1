package br.com.fiap.mottutrackapi1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VagaDTO {
    private Long id;

    @NotNull(message = "ID do Pátio é obrigatório")
    private Long idPatio; // Expose only the ID for the related Patio

    @NotBlank(message = "Código da vaga é obrigatório")
    @Size(max = 20, message = "Código da vaga deve ter no máximo 20 caracteres")
    private String codigoVaga;

    @NotBlank(message = "Tipo da vaga é obrigatório")
    @Size(max = 50, message = "Tipo da vaga deve ter no máximo 50 caracteres")
    private String tipoVaga;

    @NotBlank(message = "Status de ocupação é obrigatório")
    @Size(max = 20, message = "Status de ocupação deve ter no máximo 20 caracteres")
    private String statusOcupacao = "LIVRE";

    private BigDecimal coordenadasVagaX;
    private BigDecimal coordenadasVagaY;

    private Long idMotoOcupante; // Expose only the ID for the related Moto

    private LocalDateTime timestampUltimaOcupacao;
}

