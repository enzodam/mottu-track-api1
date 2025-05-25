package br.com.fiap.mottutrackapi1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "USUARIOS", uniqueConstraints = {
    @UniqueConstraint(columnNames = "LOGIN_USUARIO", name = "UK_USUARIOS_LOGIN"),
    @UniqueConstraint(columnNames = "EMAIL_USUARIO", name = "UK_USUARIOS_EMAIL")
})
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO")
    private Long id;

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(max = 200, message = "Nome completo deve ter no máximo 200 caracteres")
    @Column(name = "NOME_COMPLETO_USUARIO", nullable = false, length = 200)
    private String nomeCompleto;

    @NotBlank(message = "Login é obrigatório")
    @Size(max = 50, message = "Login deve ter no máximo 50 caracteres")
    @Column(name = "LOGIN_USUARIO", nullable = false, unique = true, length = 50)
    private String login;

    @NotBlank(message = "Senha é obrigatória")
    @Size(max = 255, message = "Hash da senha deve ter no máximo 255 caracteres") // Consider security implications of storing hash length
    @Column(name = "SENHA_HASH_USUARIO", nullable = false, length = 255)
    private String senhaHash;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    @Column(name = "EMAIL_USUARIO", nullable = false, unique = true, length = 100)
    private String email;


    // Filial can be null based on schema
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FILIAL", foreignKey = @ForeignKey(name = "FK_USUARIOS_FILIAL"))
    private Filial filial;

    @NotNull
    @Column(name = "ATIVO", nullable = false, columnDefinition = "NUMBER(1) DEFAULT 1")
    private Boolean ativo = true; // Default value set in Java as well

    @CreationTimestamp
    @Column(name = "DATA_CADASTRO", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "DATA_ULTIMO_LOGIN")
    private LocalDateTime dataUltimoLogin;

    @UpdateTimestamp
    @Column(name = "DATA_ULTIMA_ATUALIZACAO")
    private LocalDateTime dataUltimaAtualizacao;

    // Lombok @Data generates getters, setters, toString, equals, hashCode
}

