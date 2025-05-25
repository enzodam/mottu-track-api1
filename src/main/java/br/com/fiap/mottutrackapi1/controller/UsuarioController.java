package br.com.fiap.mottutrackapi1.controller;

import br.com.fiap.mottutrackapi1.dto.UsuarioDTO;
import br.com.fiap.mottutrackapi1.model.Filial;
import br.com.fiap.mottutrackapi1.model.Usuario;
import br.com.fiap.mottutrackapi1.repository.FilialRepository;
import br.com.fiap.mottutrackapi1.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Endpoints para gestão de usuários")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private FilialRepository filialRepository; // To fetch Filial for association

    // Helper to convert Entity to DTO
    private UsuarioDTO convertToDto(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNomeCompleto(usuario.getNomeCompleto());
        dto.setLogin(usuario.getLogin());
        dto.setEmail(usuario.getEmail());
        dto.setIdFilial(usuario.getFilial() != null ? usuario.getFilial().getId() : null);
        dto.setAtivo(usuario.getAtivo());
        dto.setDataCadastro(usuario.getDataCadastro());
        dto.setDataUltimoLogin(usuario.getDataUltimoLogin());
        dto.setDataUltimaAtualizacao(usuario.getDataUltimaAtualizacao());
        // Do NOT include senhaHash in DTO
        return dto;
    }

    // Helper to convert DTO to Entity for creation/update
    private Usuario convertToEntity(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        // ID is set by JPA or during update
        usuario.setNomeCompleto(dto.getNomeCompleto());
        usuario.setLogin(dto.getLogin());
        usuario.setEmail(dto.getEmail());
        usuario.setAtivo(dto.getAtivo());

        // Fetch and set Filial if idFilial is provided
        if (dto.getIdFilial() != null) {
            Filial filial = filialRepository.findById(dto.getIdFilial())
                    .orElseThrow(() -> new EntityNotFoundException("Filial não encontrada com ID: " + dto.getIdFilial()));
            usuario.setFilial(filial);
        }
        // Password needs to be handled separately, likely in the service during creation
        // For updates, password change should ideally be a specific endpoint/process
        return usuario;
    }

    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista de todos os usuários")
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        List<UsuarioDTO> dtos = usuarios.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Buscar usuário por ID", description = "Retorna os detalhes de um usuário específico")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id);
            return ResponseEntity.ok(convertToDto(usuario));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Cadastrar usuário", description = "Registra um novo usuário")
    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        try {
            Usuario usuario = convertToEntity(usuarioDTO);
            Usuario novoUsuario = usuarioService.cadastrar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(novoUsuario));
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Atualizar usuário", description = "Altera os dados de um usuário existente")
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioDTO usuarioDTO) {
        try {
            Usuario usuarioParaAtualizar = convertToEntity(usuarioDTO);
            // Password update should be handled separately/securely
            Usuario usuarioAtualizado = usuarioService.atualizar(id, usuarioParaAtualizar);
            return ResponseEntity.ok(convertToDto(usuarioAtualizado));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Excluir usuário", description = "Remove um usuário permanentemente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        try {
            usuarioService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

