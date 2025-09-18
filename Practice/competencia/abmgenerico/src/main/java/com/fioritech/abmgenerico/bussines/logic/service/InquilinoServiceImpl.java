package com.example.alquiler.service;

import com.example.alquiler.entity.Inquilino;
import com.example.alquiler.repository.InquilinoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InquilinoServiceImpl implements InquilinoService {

    private final InquilinoRepository repo;

    public InquilinoServiceImpl(InquilinoRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inquilino> listarActivos() {
        return repo.findByEliminadoFalseOrderByApellidoAscNombreAsc();
    }

    @Override
    public Inquilino guardar(Inquilino inquilino) {
        return repo.save(inquilino);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Inquilino> buscarPorId(Long id) {
        return repo.findById(id);
    }

    @Override
    public void eliminarLogico(Long id) {
        repo.findById(id).ifPresent(i -> {
            i.setEliminado(true);
            repo.save(i);
        });
    }

    @Override
    public void eliminarFisico(Long id) {
        repo.deleteById(id);
    }
}