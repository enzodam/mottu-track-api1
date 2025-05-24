package br.com.fiap.mottutrackapi1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "T_MOTO")
public class Moto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Placa é obrigatória")
    @Pattern(regexp = "[A-Z]{3}-\\d{4}", message = "Placa no formato AAA-1234")
    @Column(unique = true)
    private String placa;

    @NotBlank(message = "Cor é obrigatória")
    private String cor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filial_id", nullable = false)
    private Filial filial;
}