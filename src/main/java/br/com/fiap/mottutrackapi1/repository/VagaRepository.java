package br.com.fiap.mottutrackapi1.repository;

import br.com.fiap.mottutrackapi1.model.Vaga;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VagaRepository extends JpaRepository<Vaga, Long> {

    // Find vagas by Patio ID (useful for filtering)
    List<Vaga> findByPatioId(Long patioId);

    // Find vagas by Patio ID with pagination
    Page<Vaga> findByPatioId(Long patioId, Pageable pageable);

    // Find vagas by status within a specific Patio
    List<Vaga> findByPatioIdAndStatusOcupacao(Long patioId, String statusOcupacao);

    // Find vagas by status within a specific Patio with pagination
    Page<Vaga> findByPatioIdAndStatusOcupacao(Long patioId, String statusOcupacao, Pageable pageable);

    // Check if a vaga exists with a specific code within a patio (for validation)
    boolean existsByCodigoVagaAndPatioId(String codigoVaga, Long patioId);

    // Find a vaga by its code within a specific patio
    Vaga findByCodigoVagaAndPatioId(String codigoVaga, Long patioId);
}

