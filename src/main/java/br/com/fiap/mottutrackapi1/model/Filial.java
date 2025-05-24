package br.com.fiap.mottutrackapi1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "T_FILIAL")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Filial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false, unique = true)
    private String nome;

    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}", message = "CNPJ inválido")
    @Column(unique = true)
    private String cnpj;

    @NotBlank(message = "Cidade é obrigatória")
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "UF deve ter 2 caracteres")
    private String estado;

    @OneToMany(mappedBy = "filial", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Moto> motos = new ArrayList<>();
}