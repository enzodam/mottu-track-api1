package br.com.fiap.mottutrackapi1.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MotoDTO {
    private Long id;

    @NotBlank(message = "Placa é obrigatória")
    @Pattern(regexp = "[A-Z]{3}-\\d{4}", message = "Placa no formato AAA-1234")
    private String placa;

    @NotBlank(message = "Cor é obrigatória")
    private String cor;

    @NotNull(message = "ID da filial é obrigatório")
    private Long filialId;
}