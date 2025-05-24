package br.com.fiap.mottutrackapi1.service;

import br.com.fiap.mottutrackapi1.model.Moto;
import br.com.fiap.mottutrackapi1.repository.MotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

@Service
public class MotoService {

    @Autowired
    private MotoRepository motoRepository;

    @Cacheable(value = "motos")
    public Page<Moto> listarTodas(Pageable pageable) {
        return motoRepository.findAll(pageable);
    }

    @CacheEvict(value = "motos", allEntries = true)
    public Moto cadastrar(Moto moto) {
        return motoRepository.save(moto);
    }

    public Moto buscarPorId(Long id) {
        return motoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Moto não encontrada"));
    }

    @CacheEvict(value = "motos", allEntries = true)
    public Moto atualizar(Long id, Moto moto) {
        if (!motoRepository.existsById(id)) {
            throw new EntityNotFoundException("Moto não encontrada");
        }
        moto.setId(id);
        return motoRepository.save(moto);
    }

    @CacheEvict(value = "motos", allEntries = true)
    public void excluir(Long id) {
        if (!motoRepository.existsById(id)) {
            throw new EntityNotFoundException("Moto não encontrada");
        }
        motoRepository.deleteById(id);
    }
}