package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Pais;
import com.fioritech.demo.bussines.domain.Provincia;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProvinciaService {

    @PersistenceContext
    private EntityManager entityManager;

    public Provincia crearProvincia(Provincia provincia) {
        verificarAtributos(provincia);
        if (provincia.getId() != null) {
            throw new BusinessException("La provincia ya tiene un id asignado");
        }
        Pais pais = obtenerPaisActivo(provincia);
        provincia.setNombre(provincia.getNombre().trim());
        provincia.setPais(pais);
        provincia.setEliminado(false);
        entityManager.persist(provincia);
        return provincia;
    }

    public Provincia modificarProvincia(Long id, Provincia cambios) {
        Provincia existente = entityManager.find(Provincia.class, id);
        if (existente == null) {
            throw new EntityNotFoundException("No existe la provincia con id " + id);
        }
        if (existente.isEliminado()) {
            throw new BusinessException("La provincia con id " + id + " esta eliminada");
        }
        verificarAtributos(cambios);
        Pais pais = obtenerPaisActivo(cambios);
        existente.setNombre(cambios.getNombre().trim());
        existente.setPais(pais);
        return entityManager.merge(existente);
    }

    public void eliminarProvincia(Long id) {
        Provincia existente = entityManager.find(Provincia.class, id);
        if (existente == null) {
            throw new EntityNotFoundException("No existe la provincia con id " + id);
        }
        if (existente.isEliminado()) {
            throw new BusinessException("La provincia con id " + id + " ya esta eliminada");
        }
        existente.setEliminado(true);
        entityManager.merge(existente);
    }

    public void verificarAtributos(Provincia provincia) {
        if (provincia == null) {
            throw new BusinessException("La provincia es obligatoria");
        }
        if (ValidationUtils.isBlank(provincia.getNombre())) {
            throw new BusinessException("El nombre de la provincia es obligatorio");
        }
        if (provincia.getPais() == null || provincia.getPais().getId() == null) {
            throw new BusinessException("La provincia debe tener un pais asociado");
        }
    }

    private Pais obtenerPaisActivo(Provincia provincia) {
        Long paisId = provincia.getPais().getId();
        Pais pais = entityManager.find(Pais.class, paisId);
        if (pais == null) {
            throw new EntityNotFoundException("No existe el pais con id " + paisId);
        }
        if (pais.isEliminado()) {
            throw new BusinessException("El pais con id " + paisId + " esta eliminado");
        }
        return pais;
    }
}
