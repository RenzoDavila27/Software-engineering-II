package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Direccion;
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
public class DireccionService {

    @PersistenceContext
    private EntityManager entityManager;

    public Direccion crearDireccion(Direccion direccion) {
        verificarAtributos(direccion);
        if (direccion.getId() != null) {
            throw new BusinessException("La direccion ya tiene un id asignado");
        }
        Localidad localidad = obtenerLocalidadActiva(direccion);
        direccion.setCalle(direccion.getCalle().trim());
        direccion.setNumeracion(direccion.getNumeracion().trim());
        direccion.setBarrio(ajustarTexto(direccion.getBarrio()));
        direccion.setManzana(ajustarTexto(direccion.getManzana()));
        direccion.setCasaDepartamento(ajustarTexto(direccion.getCasaDepartamento()));
        direccion.setReferencia(ajustarTexto(direccion.getReferencia()));
        direccion.setLocalidad(localidad);
        direccion.setEliminado(false);
        entityManager.persist(direccion);
        return direccion;
    }

    public Direccion modificarDireccion(Long id, Direccion cambios) {
        Direccion existente = entityManager.find(Direccion.class, id);
        if (existente == null) {
            throw new EntityNotFoundException("No existe la direccion con id " + id);
        }
        if (existente.isEliminado()) {
            throw new BusinessException("La direccion con id " + id + " esta eliminada");
        }
        verificarAtributos(cambios);
        Localidad localidad = obtenerLocalidadActiva(cambios);
        existente.setCalle(cambios.getCalle().trim());
        existente.setNumeracion(cambios.getNumeracion().trim());
        existente.setBarrio(ajustarTexto(cambios.getBarrio()));
        existente.setManzana(ajustarTexto(cambios.getManzana()));
        existente.setCasaDepartamento(ajustarTexto(cambios.getCasaDepartamento()));
        existente.setReferencia(ajustarTexto(cambios.getReferencia()));
        existente.setLocalidad(localidad);
        return entityManager.merge(existente);
    }

    public void eliminarDireccion(Long id) {
        Direccion existente = entityManager.find(Direccion.class, id);
        if (existente == null) {
            throw new EntityNotFoundException("No existe la direccion con id " + id);
        }
        if (existente.isEliminado()) {
            throw new BusinessException("La direccion con id " + id + " ya esta eliminada");
        }
        existente.setEliminado(true);
        entityManager.merge(existente);
    }

    public void verificarAtributos(Direccion direccion) {
        if (direccion == null) {
            throw new BusinessException("La direccion es obligatoria");
        }
        if (ValidationUtils.isBlank(direccion.getCalle())) {
            throw new BusinessException("La calle es obligatoria");
        }
        if (ValidationUtils.isBlank(direccion.getNumeracion())) {
            throw new BusinessException("La numeracion es obligatoria");
        }
        if (direccion.getLocalidad() == null || direccion.getLocalidad().getId() == null) {
            throw new BusinessException("La direccion debe tener una localidad asociada");
        }
    }

    private Localidad obtenerLocalidadActiva(Direccion direccion) {
        Long localidadId = direccion.getLocalidad().getId();
        Localidad localidad = entityManager.find(Localidad.class, localidadId);
        if (localidad == null) {
            throw new EntityNotFoundException("No existe la localidad con id " + localidadId);
        }
        if (localidad.isEliminado()) {
            throw new BusinessException("La localidad con id " + localidadId + " esta eliminada");
        }
        return localidad;
    }

    private String ajustarTexto(String valor) {
        return valor == null ? null : valor.trim();
    }
}
