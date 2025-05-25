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
public class PatioDTO {
    private Long id;

    @NotNull(message = "ID da Filial é obrigatório")
    private Long idFilial; // Expose only the ID for the related Filial

    @NotBlank(message = "Nome do pátio é obrigatório")
    @Size(max = 100, message = "Nome do pátio deve ter no máximo 100 caracteres")
    private String nomePatio;

    @NotBlank(message = "Descrição do pátio é obrigatória")
    @Size(max = 500, message = "Descrição do pátio deve ter no máximo 500 caracteres")
    private String descricaoPatio;

    private Integer capacidadeTotalMotos;

    private BigDecimal dimensoesPatioMetrosQuadrados;

    @Size(max = 500, message = "URL da imagem do layout deve ter no máximo 500 caracteres")
    private String layoutPatioImgUrl;

    private LocalDateTime dataCadastro;
}

