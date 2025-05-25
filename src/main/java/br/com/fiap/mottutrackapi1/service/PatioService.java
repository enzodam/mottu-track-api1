package br.com.fiap.mottutrackapi1.service;

import br.com.fiap.mottutrackapi1.model.Filial;
import br.com.fiap.mottutrackapi1.model.Patio;
import br.com.fiap.mottutrackapi1.repository.FilialRepository;
import br.com.fiap.mottutrackapi1.repository.PatioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatioService {

    @Autowired
    private PatioRepository patioRepository;

    @Autowired
    private FilialRepository filialRepository; // Needed to fetch Filial for association

    public List<Patio> listarTodos() {
        return patioRepository.findAll();
    }

    public List<Patio> listarPorFilial(Long idFilial) {
        if (!filialRepository.existsById(idFilial)) {
            throw new EntityNotFoundException("Filial não encontrada com ID: " + idFilial);
        }
        return patioRepository.findByFilialId(idFilial);
    }

    public Patio buscarPorId(Long id) {
        return patioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado com ID: " + id));
    }

    public Patio cadastrar(Patio patio, Long idFilial) {
        Filial filial = filialRepository.findById(idFilial)
                .orElseThrow(() -> new EntityNotFoundException("Filial não encontrada com ID: " + idFilial));

        if (patioRepository.existsByNomePatioAndFilialId(patio.getNomePatio(), idFilial)) {
            throw new IllegalArgumentException("Já existe um pátio com o nome '" + patio.getNomePatio() + "' nesta filial.");
        }

        patio.setFilial(filial);
        return patioRepository.save(patio);
    }

    public Patio atualizar(Long id, Patio patioAtualizado, Long idFilial) {
        Patio patioExistente = buscarPorId(id); // Throws exception if not found

        // Ensure the patio belongs to the specified filial if needed, or handle filial change
        if (!patioExistente.getFilial().getId().equals(idFilial)) {
             // Option 1: Throw error if changing filial is not allowed
             // throw new IllegalArgumentException("Não é permitido alterar a filial de um pátio existente.");
             // Option 2: Fetch the new Filial and update the association
             Filial novaFilial = filialRepository.findById(idFilial)
                 .orElseThrow(() -> new EntityNotFoundException("Nova Filial não encontrada com ID: " + idFilial));
             patioExistente.setFilial(novaFilial);
        } else {
            // If filial is not changing, ensure the updated patio still references it
            patioAtualizado.setFilial(patioExistente.getFilial());
        }

        // Check for uniqueness constraint if name is updated within the same (or new) filial
        if (!patioExistente.getNomePatio().equals(patioAtualizado.getNomePatio()) &&
            patioRepository.existsByNomePatioAndFilialId(patioAtualizado.getNomePatio(), patioExistente.getFilial().getId())) {
             throw new IllegalArgumentException("Já existe um pátio com o nome '" + patioAtualizado.getNomePatio() + "' nesta filial.");
        }

        // Update fields
        patioExistente.setNomePatio(patioAtualizado.getNomePatio());
        patioExistente.setDescricaoPatio(patioAtualizado.getDescricaoPatio());
        patioExistente.setCapacidadeTotalMotos(patioAtualizado.getCapacidadeTotalMotos());
        patioExistente.setDimensoesPatioMetrosQuadrados(patioAtualizado.getDimensoesPatioMetrosQuadrados());
        patioExistente.setLayoutPatioImgUrl(patioAtualizado.getLayoutPatioImgUrl());
        // dataCadastro is usually not updated

        return patioRepository.save(patioExistente);
    }

    public void excluir(Long id) {
        if (!patioRepository.existsById(id)) {
            throw new EntityNotFoundException("Pátio não encontrado com ID: " + id);
        }
        // Consider dependencies (e.g., Vagas) before deleting
        // Add logic here if needed to check/handle related Vagas
        patioRepository.deleteById(id);
    }
}

