package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Departamento;
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
public class DepartamentoService {

    @PersistenceContext
    private EntityManager entityManager;

    public Departamento crearDepartamento(Departamento departamento) {
        verificarAtributos(departamento);
        if (departamento.getId() != null) {
            throw new BusinessException("El departamento ya tiene un id asignado");
        }
        Provincia provincia = obtenerProvinciaActiva(departamento);
        departamento.setNombre(departamento.getNombre().trim());
        departamento.setProvincia(provincia);
        departamento.setEliminado(false);
        entityManager.persist(departamento);
        return departamento;
    }

    public Departamento modificarDepartamento(Long id, Departamento cambios) {
        Departamento existente = entityManager.find(Departamento.class, id);
        if (existente == null) {
            throw new EntityNotFoundException("No existe el departamento con id " + id);
        }
        if (existente.isEliminado()) {
            throw new BusinessException("El departamento con id " + id + " esta eliminado");
        }
        verificarAtributos(cambios);
        Provincia provincia = obtenerProvinciaActiva(cambios);
        existente.setNombre(cambios.getNombre().trim());
        existente.setProvincia(provincia);
        return entityManager.merge(existente);
    }

    public void eliminarDepartamento(Long id) {
        Departamento existente = entityManager.find(Departamento.class, id);
        if (existente == null) {
            throw new EntityNotFoundException("No existe el departamento con id " + id);
        }
        if (existente.isEliminado()) {
            throw new BusinessException("El departamento con id " + id + " ya esta eliminado");
        }
        existente.setEliminado(true);
        entityManager.merge(existente);
    }

    public void verificarAtributos(Departamento departamento) {
        if (departamento == null) {
            throw new BusinessException("El departamento es obligatorio");
        }
        if (ValidationUtils.isBlank(departamento.getNombre())) {
            throw new BusinessException("El nombre del departamento es obligatorio");
        }
        if (departamento.getProvincia() == null || departamento.getProvincia().getId() == null) {
            throw new BusinessException("El departamento debe tener una provincia asociada");
        }
    }

    private Provincia obtenerProvinciaActiva(Departamento departamento) {
        Long provinciaId = departamento.getProvincia().getId();
        Provincia provincia = entityManager.find(Provincia.class, provinciaId);
        if (provincia == null) {
            throw new EntityNotFoundException("No existe la provincia con id " + provinciaId);
        }
        if (provincia.isEliminado()) {
            throw new BusinessException("La provincia con id " + provinciaId + " esta eliminada");
        }
        return provincia;
    }
}
