package br.com.fiap.mottutrackapi1.repository;

import br.com.fiap.mottutrackapi1.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Check if a user exists by login (for validation)
    boolean existsByLogin(String login);

    // Check if a user exists by email (for validation)
    boolean existsByEmail(String email);

    // Find user by login (useful for authentication or checks)
    Optional<Usuario> findByLogin(String login);

    // Find user by email (useful for password recovery or checks)
    Optional<Usuario> findByEmail(String email);
}

