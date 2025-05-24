package br.com.fiap.mottutrackapi1.controller;

import br.com.fiap.mottutrackapi1.dto.FilialDTO;
import br.com.fiap.mottutrackapi1.model.Filial;
import br.com.fiap.mottutrackapi1.repository.FilialRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/filiais")
@Tag(name = "Filiais", description = "Endpoints para gestão de filiais")
public class FilialController {

    @Autowired
    private FilialRepository filialRepository;
    private Map<String, Object> createResponse(String status, String message, String details) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("message", message);
        response.put("details", details);
        return response;
    }

    // LISTAR TODAS
    @Operation(summary = "Listar todas as filiais", description = "Retorna filiais paginadas")
    @GetMapping
    @Cacheable("filiais")
    public ResponseEntity<Page<Filial>> listarTodas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome,asc") String sort) {

        Sort.Direction direction = sort.endsWith(",desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort.split(",")[0]));
        return ResponseEntity.ok(filialRepository.findAll(pageable));
    }

    // BUSCAR POR ID
    @Operation(summary = "Buscar filial por ID", description = "Retorna os detalhes de uma filial")
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return filialRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createResponse(
                                "error",
                                "Filial não encontrada",
                                "ID " + id + " não cadastrado"
                        )));
    }

    // CADASTRAR
    @Operation(summary = "Cadastrar filial", description = "Registra uma nova filial")
    @PostMapping
    @CacheEvict(value = {"filiais", "filiaisPorEstado"}, allEntries = true)
    public ResponseEntity<Filial> cadastrar(@Valid @RequestBody FilialDTO filialDTO) {
        Filial filial = new Filial();
        filial.setNome(filialDTO.getNome());
        filial.setCnpj(filialDTO.getCnpj());
        filial.setCidade(filialDTO.getCidade());
        filial.setEstado(filialDTO.getEstado());
        return ResponseEntity.status(HttpStatus.CREATED).body(filialRepository.save(filial));
    }

    // ATUALIZAR
    @Operation(summary = "Atualizar filial", description = "Altera os dados de uma filial")
    @PutMapping("/{id}")
    @CacheEvict(value = {"filiais", "filiaisPorEstado"}, allEntries = true)
    public ResponseEntity<?> editar(
            @PathVariable Long id,
            @Valid @RequestBody FilialDTO filialDTO) {

        return filialRepository.findById(id)
                .<ResponseEntity<?>>map(filial -> {
                    filial.setNome(filialDTO.getNome());
                    filial.setCnpj(filialDTO.getCnpj());
                    filial.setCidade(filialDTO.getCidade());
                    filial.setEstado(filialDTO.getEstado());
                    return ResponseEntity.ok(filialRepository.save(filial));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createResponse(
                                "error",
                                "Atualização falhou",
                                "Filial ID " + id + " não encontrada"
                        )));
    }

    // EXCLUIR
    @Operation(summary = "Excluir filial", description = "Remove uma filial permanentemente")
    @DeleteMapping("/{id}")
    @CacheEvict(value = {"filiais", "filiaisPorEstado"}, allEntries = true)
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        return filialRepository.findById(id)
                .map(filial -> {
                    filialRepository.delete(filial);
                    return ResponseEntity.ok(createResponse(
                            "success",
                            "Filial excluída",
                            "ID " + id + " (" + filial.getNome() + ") removido"
                    ));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createResponse(
                                "error",
                                "Exclusão falhou",
                                "Filial ID " + id + " não encontrada"
                        )));
    }

    // FILTRAR POR ESTADO
    @Operation(summary = "Buscar filiais por estado", description = "Filtra por UF (ex: SP, RJ)")
    @GetMapping("/estado/{estado}")
    @Cacheable(value = "filiaisPorEstado", key = "{#estado, #page, #size, #sort}")
    public ResponseEntity<?> buscarPorEstado(
            @PathVariable String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome,asc") String sort) {

        Sort.Direction direction = sort.endsWith(",desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort.split(",")[0]));
        Page<Filial> result = filialRepository.findByEstado(estado, pageable);

        return result.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createResponse(
                        "info",
                        "Nenhuma filial encontrada",
                        "Estado: " + estado.toUpperCase()
                ))
                : ResponseEntity.ok(result);
    }
}