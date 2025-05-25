package br.com.fiap.mottutrackapi1.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(max = 200, message = "Nome completo deve ter no máximo 200 caracteres")
    private String nomeCompleto;

    @NotBlank(message = "Login é obrigatório")
    @Size(max = 50, message = "Login deve ter no máximo 50 caracteres")
    private String login;

    // Password hash should generally not be exposed in DTOs, especially for responses.
    // Consider a separate DTO for creation/update if password input is needed.
    // For now, omitting senhaHash from the standard DTO.

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    private String email;

    // ID_TIPO_USUARIO was removed based on user request.

    private Long idFilial; // Expose only the ID for the related Filial

    @NotNull
    private Boolean ativo = true;

    private LocalDateTime dataCadastro;
    private LocalDateTime dataUltimoLogin;
    private LocalDateTime dataUltimaAtualizacao;
}

