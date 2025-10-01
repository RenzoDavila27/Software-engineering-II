package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Pais;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import com.fioritech.demo.bussines.repository.PaisRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
public class PaisService {

    private final PaisRepository paisRepository;

    public PaisService(PaisRepository paisRepository) {
        this.paisRepository = paisRepository;
    }

    public Pais crearPais(Pais pais) {
        verificarAtributos(pais);
        if (pais.getId() != null) {
            throw new BusinessException("El pais ya tiene un id asignado");
        }
        pais.setNombre(pais.getNombre().trim());
        pais.setEliminado(false);
        return paisRepository.save(pais);
    }

    public Pais modificarPais(Long id, Pais cambios) {
        Pais existente = obtenerPaisActivo(id);
        verificarAtributos(cambios);
        existente.setNombre(cambios.getNombre().trim());
        return paisRepository.save(existente);
    }

    public void eliminarPais(Long id) {
        Pais existente = obtenerPaisActivo(id);
        existente.setEliminado(true);
        paisRepository.save(existente);
    }

    @Transactional(readOnly = true)
    public Collection<Pais> listarPaises() {
        return paisRepository.buscarPaisesActivos();
    }

    @Transactional(readOnly = true)
    public Pais buscarPaisPorId(Long id) {
        return obtenerPaisActivo(id);
    }

    public void verificarAtributos(Pais pais) {
        if (pais == null) {
            throw new BusinessException("El pais es obligatorio");
        }
        if (ValidationUtils.isBlank(pais.getNombre())) {
            throw new BusinessException("El nombre del pais es obligatorio");
        }
    }

    private Pais obtenerPaisActivo(Long id) {
        Pais pais = paisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe el pais con id " + id));
        if (pais.isEliminado()) {
            throw new BusinessException("El pais con id " + id + " esta eliminado");
        }
        return pais;
    }
}
