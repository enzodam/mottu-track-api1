package br.com.fiap.mottutrackapi1.controller;

import br.com.fiap.mottutrackapi1.dto.VagaDTO;
import br.com.fiap.mottutrackapi1.model.Moto;
import br.com.fiap.mottutrackapi1.model.Patio;
import br.com.fiap.mottutrackapi1.model.Vaga;
import br.com.fiap.mottutrackapi1.repository.MotoRepository;
import br.com.fiap.mottutrackapi1.repository.PatioRepository;
import br.com.fiap.mottutrackapi1.service.VagaService;
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
@RequestMapping("/api/vagas")
@Tag(name = "Vagas", description = "Endpoints para gestão de vagas")
public class VagaController {

    @Autowired
    private VagaService vagaService;

    @Autowired
    private PatioRepository patioRepository; // Needed for association in convertToEntity

    @Autowired
    private MotoRepository motoRepository; // Needed for association in convertToEntity

    // Helper to convert Entity to DTO
    private VagaDTO convertToDto(Vaga vaga) {
        VagaDTO dto = new VagaDTO();
        dto.setId(vaga.getId());
        dto.setIdPatio(vaga.getPatio() != null ? vaga.getPatio().getId() : null);
        dto.setCodigoVaga(vaga.getCodigoVaga());
        dto.setTipoVaga(vaga.getTipoVaga());
        dto.setStatusOcupacao(vaga.getStatusOcupacao());
        dto.setCoordenadasVagaX(vaga.getCoordenadasVagaX());
        dto.setCoordenadasVagaY(vaga.getCoordenadasVagaY());
        dto.setIdMotoOcupante(vaga.getMotoOcupante() != null ? vaga.getMotoOcupante().getId() : null);
        dto.setTimestampUltimaOcupacao(vaga.getTimestampUltimaOcupacao());
        return dto;
    }

    // Helper to convert DTO to Entity for creation/update (basic fields)
    private Vaga convertToEntity(VagaDTO dto) {
        Vaga vaga = new Vaga();
        // ID is set by JPA or during update
        vaga.setCodigoVaga(dto.getCodigoVaga());
        vaga.setTipoVaga(dto.getTipoVaga());
        // Status, Patio, MotoOcupante are handled in the service layer
        vaga.setCoordenadasVagaX(dto.getCoordenadasVagaX());
        vaga.setCoordenadasVagaY(dto.getCoordenadasVagaY());
        return vaga;
    }

    @Operation(summary = "Listar todas as vagas", description = "Retorna uma lista de todas as vagas")
    @GetMapping
    public ResponseEntity<List<VagaDTO>> listarTodas() {
        List<Vaga> vagas = vagaService.listarTodas();
        List<VagaDTO> dtos = vagas.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Listar vagas por pátio", description = "Retorna uma lista de vagas pertencentes a um pátio específico")
    @GetMapping("/patio/{idPatio}")
    public ResponseEntity<?> listarPorPatio(@PathVariable Long idPatio) {
        try {
            List<Vaga> vagas = vagaService.listarPorPatio(idPatio);
            List<VagaDTO> dtos = vagas.stream().map(this::convertToDto).collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Buscar vaga por ID", description = "Retorna os detalhes de uma vaga específica")
    @GetMapping("/{id}")
    public ResponseEntity<VagaDTO> buscarPorId(@PathVariable Long id) {
        try {
            Vaga vaga = vagaService.buscarPorId(id);
            return ResponseEntity.ok(convertToDto(vaga));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Cadastrar vaga", description = "Registra uma nova vaga associada a um pátio")
    @PostMapping
    public ResponseEntity<?> cadastrar(
            @Parameter(description = "ID do pátio ao qual a vaga pertence", required = true) @RequestParam Long idPatio,
            @Valid @RequestBody VagaDTO vagaDTO) {
        try {
            Vaga vaga = convertToEntity(vagaDTO);
            Vaga novaVaga = vagaService.cadastrar(vaga, idPatio);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(novaVaga));
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Atualizar vaga", description = "Altera os dados de uma vaga existente (exceto status/ocupação)")
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @Parameter(description = "ID do pátio (pode ser o mesmo ou um novo ID se a mudança for permitida)", required = true) @RequestParam Long idPatio,
            @Valid @RequestBody VagaDTO vagaDTO) {
        try {
            Vaga vagaParaAtualizar = convertToEntity(vagaDTO);
            Vaga vagaAtualizada = vagaService.atualizar(id, vagaParaAtualizar, idPatio);
            return ResponseEntity.ok(convertToDto(vagaAtualizada));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Ocupar vaga", description = "Marca uma vaga como ocupada por uma moto específica")
    @PostMapping("/{id}/ocupar")
    public ResponseEntity<?> ocuparVaga(
            @PathVariable Long id,
            @Parameter(description = "ID da moto que ocupará a vaga", required = true) @RequestParam Long idMoto) {
        try {
            Vaga vagaOcupada = vagaService.ocuparVaga(id, idMoto);
            return ResponseEntity.ok(convertToDto(vagaOcupada));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Operation(summary = "Liberar vaga", description = "Marca uma vaga como livre")
    @PostMapping("/{id}/liberar")
    public ResponseEntity<?> liberarVaga(@PathVariable Long id) {
        try {
            Vaga vagaLiberada = vagaService.liberarVaga(id);
            return ResponseEntity.ok(convertToDto(vagaLiberada));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Operation(summary = "Excluir vaga", description = "Remove uma vaga permanentemente (somente se estiver livre)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        try {
            vagaService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}

