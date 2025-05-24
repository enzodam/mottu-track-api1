package br.com.fiap.mottutrackapi1.repository;

import br.com.fiap.mottutrackapi1.model.Filial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FilialRepository extends JpaRepository<Filial, Long> {

    @Query("SELECT f FROM Filial f WHERE f.estado = :estado")
    Page<Filial> findByEstado(@Param("estado") String estado, Pageable pageable);

    boolean existsByCnpj(String cnpj);

    @Query("SELECT f FROM Filial f LEFT JOIN FETCH f.motos WHERE f.id = :id")
    Filial findByIdWithMotos(@Param("id") Long id);
}