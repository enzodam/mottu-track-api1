package br.com.fiap.mottutrackapi1.service;

import br.com.fiap.mottutrackapi1.model.Moto;
import br.com.fiap.mottutrackapi1.model.Patio;
import br.com.fiap.mottutrackapi1.model.Vaga;
import br.com.fiap.mottutrackapi1.repository.MotoRepository;
import br.com.fiap.mottutrackapi1.repository.PatioRepository;
import br.com.fiap.mottutrackapi1.repository.VagaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VagaService {

    @Autowired
    private VagaRepository vagaRepository;

    @Autowired
    private PatioRepository patioRepository;

    @Autowired
    private MotoRepository motoRepository; // Needed if associating Moto

    public List<Vaga> listarTodas() {
        return vagaRepository.findAll();
    }

    public List<Vaga> listarPorPatio(Long idPatio) {
        if (!patioRepository.existsById(idPatio)) {
            throw new EntityNotFoundException("Pátio não encontrado com ID: " + idPatio);
        }
        return vagaRepository.findByPatioId(idPatio);
    }

    public Vaga buscarPorId(Long id) {
        return vagaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada com ID: " + id));
    }

    @Transactional // Ensure atomicity when creating
    public Vaga cadastrar(Vaga vaga, Long idPatio) {
        Patio patio = patioRepository.findById(idPatio)
                .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado com ID: " + idPatio));

        if (vagaRepository.existsByCodigoVagaAndPatioId(vaga.getCodigoVaga(), idPatio)) {
            throw new IllegalArgumentException("Já existe uma vaga com o código '" + vaga.getCodigoVaga() + "' neste pátio.");
        }

        vaga.setPatio(patio);
        vaga.setStatusOcupacao("LIVRE"); // Ensure initial status is LIVRE
        vaga.setMotoOcupante(null);
        vaga.setTimestampUltimaOcupacao(null);

        return vagaRepository.save(vaga);
    }

    @Transactional // Ensure atomicity when updating
    public Vaga atualizar(Long id, Vaga vagaAtualizada, Long idPatio) {
        Vaga vagaExistente = buscarPorId(id); // Throws exception if not found

        // Check if Patio is being changed
        if (!vagaExistente.getPatio().getId().equals(idPatio)) {
            Patio novoPatio = patioRepository.findById(idPatio)
                    .orElseThrow(() -> new EntityNotFoundException("Novo Pátio não encontrado com ID: " + idPatio));
            vagaExistente.setPatio(novoPatio);
        } else {
            // Ensure the updated vaga still references the correct patio
            vagaAtualizada.setPatio(vagaExistente.getPatio());
        }

        // Check for uniqueness constraint if code is updated within the same (or new) patio
        if (!vagaExistente.getCodigoVaga().equals(vagaAtualizada.getCodigoVaga()) &&
            vagaRepository.existsByCodigoVagaAndPatioId(vagaAtualizada.getCodigoVaga(), vagaExistente.getPatio().getId())) {
             throw new IllegalArgumentException("Já existe uma vaga com o código '" + vagaAtualizada.getCodigoVaga() + "' neste pátio.");
        }

        // Update fields (status/motoOcupante might be handled by separate methods)
        vagaExistente.setCodigoVaga(vagaAtualizada.getCodigoVaga());
        vagaExistente.setTipoVaga(vagaAtualizada.getTipoVaga());
        vagaExistente.setCoordenadasVagaX(vagaAtualizada.getCoordenadasVagaX());
        vagaExistente.setCoordenadasVagaY(vagaAtualizada.getCoordenadasVagaY());
        // Status, MotoOcupante, and Timestamp are typically managed by specific operations (occupy/free)

        return vagaRepository.save(vagaExistente);
    }

    @Transactional
    public Vaga ocuparVaga(Long idVaga, Long idMoto) {
        Vaga vaga = buscarPorId(idVaga);
        if (!"LIVRE".equals(vaga.getStatusOcupacao())) {
            throw new IllegalStateException("Vaga " + vaga.getCodigoVaga() + " já está ocupada.");
        }
        Moto moto = motoRepository.findById(idMoto)
                .orElseThrow(() -> new EntityNotFoundException("Moto não encontrada com ID: " + idMoto));

        vaga.setStatusOcupacao("OCUPADA");
        vaga.setMotoOcupante(moto);
        vaga.setTimestampUltimaOcupacao(LocalDateTime.now());
        return vagaRepository.save(vaga);
    }

    @Transactional
    public Vaga liberarVaga(Long idVaga) {
        Vaga vaga = buscarPorId(idVaga);
        if (!"OCUPADA".equals(vaga.getStatusOcupacao())) {
            // Optionally allow freeing if status is something else, or throw error
            throw new IllegalStateException("Vaga " + vaga.getCodigoVaga() + " não está ocupada.");
        }
        vaga.setStatusOcupacao("LIVRE");
        vaga.setMotoOcupante(null);
        // Keep timestamp or clear it based on requirements
        // vaga.setTimestampUltimaOcupacao(null);
        return vagaRepository.save(vaga);
    }

    @Transactional
    public void excluir(Long id) {
        Vaga vaga = buscarPorId(id);
        if ("OCUPADA".equals(vaga.getStatusOcupacao())) {
            throw new IllegalStateException("Não é possível excluir uma vaga ocupada. Libere a vaga primeiro.");
        }
        vagaRepository.deleteById(id);
    }
}

