package br.com.fiap.mottutrackapi1.service;

import br.com.fiap.mottutrackapi1.model.Usuario;
import br.com.fiap.mottutrackapi1.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
// Consider adding PasswordEncoder if password hashing is needed
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Optional: Inject PasswordEncoder if handling password hashing
    // @Autowired
    // private PasswordEncoder passwordEncoder;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
    }

    public Usuario cadastrar(Usuario usuario) {
        // Basic validation example (can be expanded)
        if (usuarioRepository.existsByLogin(usuario.getLogin())) {
            throw new IllegalArgumentException("Login já existe: " + usuario.getLogin());
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Email já existe: " + usuario.getEmail());
        }

        // Optional: Hash password before saving
        // usuario.setSenhaHash(passwordEncoder.encode(usuario.getSenhaHash()));

        return usuarioRepository.save(usuario);
    }

    public Usuario atualizar(Long id, Usuario usuarioAtualizado) {
        Usuario usuarioExistente = buscarPorId(id); // Throws exception if not found

        // Check for uniqueness constraints if login/email are updated
        if (!usuarioExistente.getLogin().equals(usuarioAtualizado.getLogin()) && usuarioRepository.existsByLogin(usuarioAtualizado.getLogin())) {
             throw new IllegalArgumentException("Login já existe: " + usuarioAtualizado.getLogin());
        }
        if (!usuarioExistente.getEmail().equals(usuarioAtualizado.getEmail()) && usuarioRepository.existsByEmail(usuarioAtualizado.getEmail())) {
             throw new IllegalArgumentException("Email já existe: " + usuarioAtualizado.getEmail());
        }

        // Update fields (consider which fields are updatable)
        usuarioExistente.setNomeCompleto(usuarioAtualizado.getNomeCompleto());
        usuarioExistente.setLogin(usuarioAtualizado.getLogin());
        usuarioExistente.setEmail(usuarioAtualizado.getEmail());
        usuarioExistente.setFilial(usuarioAtualizado.getFilial()); // Assuming Filial object is provided or fetched
        usuarioExistente.setAtivo(usuarioAtualizado.getAtivo());

        // Optional: Handle password update separately and securely
        // if (usuarioAtualizado.getSenhaHash() != null && !usuarioAtualizado.getSenhaHash().isEmpty()) {
        //     usuarioExistente.setSenhaHash(passwordEncoder.encode(usuarioAtualizado.getSenhaHash()));
        // }

        return usuarioRepository.save(usuarioExistente);
    }

    public void excluir(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuário não encontrado com ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}

