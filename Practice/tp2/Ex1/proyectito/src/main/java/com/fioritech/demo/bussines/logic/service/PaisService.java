package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Pais;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaisService {

    @PersistenceContext
    private EntityManager entityManager;

    public Pais crearPais(Pais pais) {
        verificarAtributos(pais);
        if (pais.getId() != null) {
            throw new BusinessException("El pais ya tiene un id asignado");
        }
        pais.setNombre(pais.getNombre().trim());
        pais.setEliminado(false);
        entityManager.persist(pais);
        return pais;
    }

    public Pais modificarPais(Long id, Pais cambios) {
        Pais existente = entityManager.find(Pais.class, id);
        if (existente == null) {
            throw new EntityNotFoundException("No existe el pais con id " + id);
        }
        if (existente.isEliminado()) {
            throw new BusinessException("El pais con id " + id + " esta eliminado");
        }
        verificarAtributos(cambios);
        existente.setNombre(cambios.getNombre().trim());
        return entityManager.merge(existente);
    }

    public void eliminarPais(Long id) {
        Pais existente = entityManager.find(Pais.class, id);
        if (existente == null) {
            throw new EntityNotFoundException("No existe el pais con id " + id);
        }
        if (existente.isEliminado()) {
            throw new BusinessException("El pais con id " + id + " ya esta eliminado");
        }
        existente.setEliminado(true);
        entityManager.merge(existente);
    }

    public void verificarAtributos(Pais pais) {
        if (pais == null) {
            throw new BusinessException("El pais es obligatorio");
        }
        if (ValidationUtils.isBlank(pais.getNombre())) {
            throw new BusinessException("El nombre del pais es obligatorio");
        }
    }
}
