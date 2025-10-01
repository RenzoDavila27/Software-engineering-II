package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Departamento;
import com.fioritech.demo.bussines.domain.Localidad;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LocalidadService {

    @PersistenceContext
    private EntityManager entityManager;

    public Localidad crearLocalidad(Localidad localidad) {
        verificarAtributos(localidad);
        if (localidad.getId() != null) {
            throw new BusinessException("La localidad ya tiene un id asignado");
        }
        Departamento departamento = obtenerDepartamentoActivo(localidad);
        localidad.setNombre(localidad.getNombre().trim());
        localidad.setCodPostal(localidad.getCodPostal().trim());
        localidad.setDepartamento(departamento);
        localidad.setEliminado(false);
        entityManager.persist(localidad);
        return localidad;
    }

    public Localidad modificarLocalidad(Long id, Localidad cambios) {
        Localidad existente = entityManager.find(Localidad.class, id);
        if (existente == null) {
            throw new EntityNotFoundException("No existe la localidad con id " + id);
        }
        if (existente.isEliminado()) {
            throw new BusinessException("La localidad con id " + id + " esta eliminada");
        }
        verificarAtributos(cambios);
        Departamento departamento = obtenerDepartamentoActivo(cambios);
        existente.setNombre(cambios.getNombre().trim());
        existente.setCodPostal(cambios.getCodPostal().trim());
        existente.setDepartamento(departamento);
        return entityManager.merge(existente);
    }

    public void eliminarLocalidad(Long id) {
        Localidad existente = entityManager.find(Localidad.class, id);
        if (existente == null) {
            throw new EntityNotFoundException("No existe la localidad con id " + id);
        }
        if (existente.isEliminado()) {
            throw new BusinessException("La localidad con id " + id + " ya esta eliminada");
        }
        existente.setEliminado(true);
        entityManager.merge(existente);
    }

    public void verificarAtributos(Localidad localidad) {
        if (localidad == null) {
            throw new BusinessException("La localidad es obligatoria");
        }
        if (ValidationUtils.isBlank(localidad.getNombre())) {
            throw new BusinessException("El nombre de la localidad es obligatorio");
        }
        if (ValidationUtils.isBlank(localidad.getCodPostal())) {
            throw new BusinessException("El codigo postal es obligatorio");
        }
        if (localidad.getDepartamento() == null || localidad.getDepartamento().getId() == null) {
            throw new BusinessException("La localidad debe tener un departamento asociado");
        }
    }

    private Departamento obtenerDepartamentoActivo(Localidad localidad) {
        Long departamentoId = localidad.getDepartamento().getId();
        Departamento departamento = entityManager.find(Departamento.class, departamentoId);
        if (departamento == null) {
            throw new EntityNotFoundException("No existe el departamento con id " + departamentoId);
        }
        if (departamento.isEliminado()) {
            throw new BusinessException("El departamento con id " + departamentoId + " esta eliminado");
        }
        return departamento;
    }
}
