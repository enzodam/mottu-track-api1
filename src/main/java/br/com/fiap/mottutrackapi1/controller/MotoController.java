package br.com.fiap.mottutrackapi1.controller;

import br.com.fiap.mottutrackapi1.dto.MotoDTO;
import br.com.fiap.mottutrackapi1.model.Filial;
import br.com.fiap.mottutrackapi1.model.Moto;
import br.com.fiap.mottutrackapi1.repository.FilialRepository;
import br.com.fiap.mottutrackapi1.repository.MotoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/motos")
@Tag(name = "Motos", description = "Endpoints para gestão de motos")
public class MotoController {

    @Autowired
    private MotoRepository motoRepository;

    @Autowired
    private FilialRepository filialRepository;

    // LISTAR TODAS
    @Operation(summary = "Listar todas as motos", description = "Retorna motos paginadas, ordenadas por placa (ascendente)")
    @GetMapping
    @Cacheable("motos")
    public ResponseEntity<Page<Moto>> listarTodas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "placa,asc") String sort) {

        Sort.Direction direction = sort.endsWith(",desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort.split(",")[0]));
        return ResponseEntity.ok(motoRepository.findAllWithFilial(pageable));
    }

    // BUSCAR POR ESTADO - COM MENSAGEM DE ERRO
    @Operation(summary = "Buscar motos por estado", description = "Filtra motos pelo estado da filial")
    @GetMapping("/estado/{estado}")
    @Cacheable(value = "motosPorEstado", key = "{#estado, #page, #size, #sort}")
    public ResponseEntity<?> buscarPorEstado(
            @PathVariable String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "placa,asc") String sort) {

        Sort.Direction direction = sort.endsWith(",desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort.split(",")[0]));
        Page<Moto> result = motoRepository.findByEstadoWithFilial(estado, pageable);

        return result.isEmpty() ?
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma moto encontrada no estado " + estado) :
                ResponseEntity.ok(result);
    }

    // CADASTRAR
    @Operation(summary = "Cadastrar moto", description = "Registra uma nova moto (valida placa)")
    @PostMapping
    @CacheEvict(value = {"motos", "motosPorEstado"}, allEntries = true)
    public ResponseEntity<Moto> cadastrar(@Valid @RequestBody MotoDTO motoDTO) {
        if (motoRepository.existsByPlaca(motoDTO.getPlaca())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Placa já cadastrada");
        }

        Moto moto = new Moto();
        moto.setPlaca(motoDTO.getPlaca());
        moto.setCor(motoDTO.getCor());

        Filial filial = filialRepository.findById(motoDTO.getFilialId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filial não encontrada"));
        moto.setFilial(filial);

        return ResponseEntity.status(HttpStatus.CREATED).body(motoRepository.save(moto));
    }

    // BUSCAR POR ID
    @Operation(summary = "Buscar moto por ID", description = "Retorna uma moto específica")
    @GetMapping("/{id}")
    @Cacheable(value = "moto", key = "#id")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Moto> motoOptional = motoRepository.findByIdWithFilial(id);

        if (motoOptional.isPresent()) {
            return ResponseEntity.ok(motoOptional.get());
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Moto com ID " + id + " não encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // ATUALIZAR
    @Operation(summary = "Atualizar moto", description = "Atualiza os dados de uma moto existente")
    @PutMapping("/{id}")
    @CacheEvict(value = {"motos", "motosPorEstado", "moto"}, allEntries = true)
    public ResponseEntity<?> editar(
            @PathVariable Long id,
            @Valid @RequestBody MotoDTO motoDTO) {

        // 1. Verifica se a moto existe
        return motoRepository.findByIdWithFilial(id)
                .map(motoExistente -> {
                    try {
                        // 2. Validação da placa (se foi alterada)
                        if (!motoExistente.getPlaca().equals(motoDTO.getPlaca()) &&
                                motoRepository.existsByPlaca(motoDTO.getPlaca())) {
                            return ResponseEntity.badRequest()
                                    .body(Map.of(
                                            "status", "error",
                                            "message", "Placa duplicada",
                                            "details", "A placa " + motoDTO.getPlaca() + " já está em uso"
                                    ));
                        }

                        // 3. Atualiza os dados básicos
                        motoExistente.setPlaca(motoDTO.getPlaca());
                        motoExistente.setCor(motoDTO.getCor());

                        // 4. Atualiza a filial (se necessário)
                        if (!motoExistente.getFilial().getId().equals(motoDTO.getFilialId())) {
                            Filial novaFilial = filialRepository.findById(motoDTO.getFilialId())
                                    .orElseThrow(() -> new ResponseStatusException(
                                            HttpStatus.BAD_REQUEST,
                                            "Filial ID " + motoDTO.getFilialId() + " não encontrada"
                                    ));
                            motoExistente.setFilial(novaFilial);
                        }

                        // 5. Salva e recarrega a moto atualizada
                        Moto motoAtualizada = motoRepository.save(motoExistente);
                        motoAtualizada = motoRepository.findByIdWithFilial(motoAtualizada.getId()).get();

                        // 6. Retorna sucesso
                        return ResponseEntity.ok(motoAtualizada);

                    } catch (ResponseStatusException e) {
                        return ResponseEntity.status(e.getStatusCode())
                                .body(Map.of(
                                        "status", "error",
                                        "message", "Erro na filial",
                                        "details", e.getReason()
                                ));
                    } catch (Exception e) {
                        return ResponseEntity.internalServerError()
                                .body(Map.of(
                                        "status", "error",
                                        "message", "Erro na atualização",
                                        "details", e.getMessage()
                                ));
                    }
                })
                // 7. Caso a moto não exista
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "status", "error",
                                "message", "Moto não encontrada",
                                "details", "ID " + id + " não cadastrado no sistema"
                        )));
    }

    // DELETAR
    @Operation(summary = "Excluir moto", description = "Remove permanentemente uma moto pelo ID")
    @DeleteMapping("/{id}")
    @CacheEvict(value = {"motos", "motosPorEstado", "moto"}, allEntries = true)
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        try {
            if (!motoRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Moto com ID " + id + " não encontrada");
            }
            motoRepository.deleteById(id);
            return ResponseEntity.ok()
                    .body("Moto com ID " + id + " foi excluída com sucesso");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao excluir moto: " + e.getMessage());
        }
    }
}