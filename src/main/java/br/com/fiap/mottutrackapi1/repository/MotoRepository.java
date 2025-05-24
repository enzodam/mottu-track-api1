package br.com.fiap.mottutrackapi1.repository;

import br.com.fiap.mottutrackapi1.model.Moto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // CORRIGIDO: Import do Spring
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MotoRepository extends JpaRepository<Moto, Long> {

    @EntityGraph(attributePaths = {"filial"})
    @Query("SELECT m FROM Moto m WHERE m.id = :id")
    Optional<Moto> findByIdWithFilial(@Param("id") Long id);

    @EntityGraph(attributePaths = {"filial"})
    @Query("SELECT m FROM Moto m")
    Page<Moto> findAllWithFilial(Pageable pageable);

    @EntityGraph(attributePaths = {"filial"})
    @Query("SELECT m FROM Moto m WHERE m.filial.estado = :estado")
    Page<Moto> findByEstadoWithFilial(@Param("estado") String estado, Pageable pageable);

    boolean existsByPlaca(String placa);
}