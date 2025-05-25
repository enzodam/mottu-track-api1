package br.com.fiap.mottutrackapi1.repository;

import br.com.fiap.mottutrackapi1.model.Patio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatioRepository extends JpaRepository<Patio, Long> {

    // Find patios by Filial ID (useful for filtering)
    List<Patio> findByFilialId(Long filialId);

    // Find patios by Filial ID with pagination
    Page<Patio> findByFilialId(Long filialId, Pageable pageable);

    // Check if a patio exists with a specific name within a filial (for validation)
    boolean existsByNomePatioAndFilialId(String nomePatio, Long filialId);
}

