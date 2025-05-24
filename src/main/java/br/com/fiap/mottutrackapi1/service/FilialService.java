package br.com.fiap.mottutrackapi1.service;

import br.com.fiap.mottutrackapi1.model.Filial;
import br.com.fiap.mottutrackapi1.repository.FilialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

@Service
public class FilialService {

    @Autowired
    private FilialRepository filialRepository;

    @Cacheable(value = "filiais")
    public Iterable<Filial> listarTodas() {
        return filialRepository.findAll();
    }

    @CacheEvict(value = "filiais", allEntries = true)
    public Filial cadastrar(Filial filial) {
        return filialRepository.save(filial);
    }

    public Filial buscarPorId(Long id) {
        return filialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Filial não encontrada"));
    }

    @CacheEvict(value = "filiais", allEntries = true)
    public Filial atualizar(Long id, Filial filial) {
        if (!filialRepository.existsById(id)) {
            throw new EntityNotFoundException("Filial não encontrada");
        }
        filial.setId(id);
        return filialRepository.save(filial);
    }

    @CacheEvict(value = "filiais", allEntries = true)
    public void excluir(Long id) {
        if (!filialRepository.existsById(id)) {
            throw new EntityNotFoundException("Filial não encontrada");
        }
        filialRepository.deleteById(id);
    }
}