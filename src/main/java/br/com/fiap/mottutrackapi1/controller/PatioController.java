package br.com.fiap.mottutrackapi1.controller;

import br.com.fiap.mottutrackapi1.dto.PatioDTO;
import br.com.fiap.mottutrackapi1.model.Filial;
import br.com.fiap.mottutrackapi1.model.Patio;
import br.com.fiap.mottutrackapi1.repository.FilialRepository;
import br.com.fiap.mottutrackapi1.service.PatioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/patios")
@Tag(name = "Patios", description = "Endpoints para gestão de pátios")
public class PatioController {

    @Autowired
    private PatioService patioService;

    @Autowired
    private FilialRepository filialRepository; // Needed for association

    // Helper to convert Entity to DTO
    private PatioDTO convertToDto(Patio patio) {
        PatioDTO dto = new PatioDTO();
        dto.setId(patio.getId());
        dto.setIdFilial(patio.getFilial() != null ? patio.getFilial().getId() : null);
        dto.setNomePatio(patio.getNomePatio());
        dto.setDescricaoPatio(patio.getDescricaoPatio());
        dto.setCapacidadeTotalMotos(patio.getCapacidadeTotalMotos());
        dto.setDimensoesPatioMetrosQuadrados(patio.getDimensoesPatioMetrosQuadrados());
        dto.setLayoutPatioImgUrl(patio.getLayoutPatioImgUrl());
        dto.setDataCadastro(patio.getDataCadastro());
        return dto;
    }

    // Helper to convert DTO to Entity for creation/update
    private Patio convertToEntity(PatioDTO dto) {
        Patio patio = new Patio();
        // ID is set by JPA or during update
        patio.setNomePatio(dto.getNomePatio());
        patio.setDescricaoPatio(dto.getDescricaoPatio());
        patio.setCapacidadeTotalMotos(dto.getCapacidadeTotalMotos());
        patio.setDimensoesPatioMetrosQuadrados(dto.getDimensoesPatioMetrosQuadrados());
        patio.setLayoutPatioImgUrl(dto.getLayoutPatioImgUrl());
        // Filial association is handled in the service layer during create/update
        return patio;
    }

    @Operation(summary = "Listar todos os pátios", description = "Retorna uma lista de todos os pátios")
    @GetMapping
    public ResponseEntity<List<PatioDTO>> listarTodos() {
        List<Patio> patios = patioService.listarTodos();
        List<PatioDTO> dtos = patios.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Listar pátios por filial", description = "Retorna uma lista de pátios pertencentes a uma filial específica")
    @GetMapping("/filial/{idFilial}")
    public ResponseEntity<?> listarPorFilial(@PathVariable Long idFilial) {
        try {
            List<Patio> patios = patioService.listarPorFilial(idFilial);
            List<PatioDTO> dtos = patios.stream().map(this::convertToDto).collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Buscar pátio por ID", description = "Retorna os detalhes de um pátio específico")
    @GetMapping("/{id}")
    public ResponseEntity<PatioDTO> buscarPorId(@PathVariable Long id) {
        try {
            Patio patio = patioService.buscarPorId(id);
            return ResponseEntity.ok(convertToDto(patio));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Cadastrar pátio", description = "Registra um novo pátio associado a uma filial")
    @PostMapping
    public ResponseEntity<?> cadastrar(
            @Parameter(description = "ID da filial à qual o pátio pertence", required = true) @RequestParam Long idFilial,
            @Valid @RequestBody PatioDTO patioDTO) {
        try {
            Patio patio = convertToEntity(patioDTO);
            Patio novoPatio = patioService.cadastrar(patio, idFilial);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(novoPatio));
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Atualizar pátio", description = "Altera os dados de um pátio existente")
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @Parameter(description = "ID da filial (pode ser o mesmo ou um novo ID se a mudança for permitida)", required = true) @RequestParam Long idFilial,
            @Valid @RequestBody PatioDTO patioDTO) {
        try {
            Patio patioParaAtualizar = convertToEntity(patioDTO);
            Patio patioAtualizado = patioService.atualizar(id, patioParaAtualizar, idFilial);
            return ResponseEntity.ok(convertToDto(patioAtualizado));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Excluir pátio", description = "Remove um pátio permanentemente (cuidado com dependências como vagas)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        try {
            patioService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) { // Catch potential dependency issues or other errors
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro ao excluir pátio: " + e.getMessage());
        }
    }
}

