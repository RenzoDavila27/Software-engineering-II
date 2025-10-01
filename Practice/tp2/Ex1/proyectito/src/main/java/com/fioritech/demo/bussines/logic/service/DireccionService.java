package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Direccion;
import com.fioritech.demo.bussines.domain.Localidad;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import com.fioritech.demo.bussines.repository.DireccionRepository;
import com.fioritech.demo.bussines.repository.LocalidadRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
public class DireccionService {

    private final DireccionRepository direccionRepository;
    private final LocalidadRepository localidadRepository;

    public DireccionService(DireccionRepository direccionRepository,
                            LocalidadRepository localidadRepository) {
        this.direccionRepository = direccionRepository;
        this.localidadRepository = localidadRepository;
    }

    public Direccion crearDireccion(Direccion direccion) {
        verificarAtributos(direccion);
        if (direccion.getId() != null) {
            throw new BusinessException("La direccion ya tiene un id asignado");
        }
        Localidad localidad = obtenerLocalidadActiva(direccion.getLocalidad().getId());
        direccion.setCalle(direccion.getCalle().trim());
        direccion.setNumeracion(direccion.getNumeracion().trim());
        direccion.setBarrio(ajustarTexto(direccion.getBarrio()));
        direccion.setManzana(ajustarTexto(direccion.getManzana()));
        direccion.setCasaDepartamento(ajustarTexto(direccion.getCasaDepartamento()));
        direccion.setReferencia(ajustarTexto(direccion.getReferencia()));
        direccion.setLocalidad(localidad);
        direccion.setEliminado(false);
        return direccionRepository.save(direccion);
    }

    public Direccion modificarDireccion(Long id, Direccion cambios) {
        Direccion existente = obtenerDireccionActiva(id);
        verificarAtributos(cambios);
        Localidad localidad = obtenerLocalidadActiva(cambios.getLocalidad().getId());
        existente.setCalle(cambios.getCalle().trim());
        existente.setNumeracion(cambios.getNumeracion().trim());
        existente.setBarrio(ajustarTexto(cambios.getBarrio()));
        existente.setManzana(ajustarTexto(cambios.getManzana()));
        existente.setCasaDepartamento(ajustarTexto(cambios.getCasaDepartamento()));
        existente.setReferencia(ajustarTexto(cambios.getReferencia()));
        existente.setLocalidad(localidad);
        return direccionRepository.save(existente);
    }

    public void eliminarDireccion(Long id) {
        Direccion existente = obtenerDireccionActiva(id);
        existente.setEliminado(true);
        direccionRepository.save(existente);
    }

    @Transactional(readOnly = true)
    public Collection<Direccion> listarDirecciones() {
        return direccionRepository.buscarDireccionesActivas();
    }

    @Transactional(readOnly = true)
    public Direccion buscarDireccionPorId(Long id) {
        return obtenerDireccionActiva(id);
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

    private Direccion obtenerDireccionActiva(Long id) {
        Direccion direccion = direccionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe la direccion con id " + id));
        if (direccion.isEliminado()) {
            throw new BusinessException("La direccion con id " + id + " esta eliminada");
        }
        return direccion;
    }

    private Localidad obtenerLocalidadActiva(Long localidadId) {
        Localidad localidad = localidadRepository.findById(localidadId)
                .orElseThrow(() -> new EntityNotFoundException("No existe la localidad con id " + localidadId));
        if (localidad.isEliminado()) {
            throw new BusinessException("La localidad con id " + localidadId + " esta eliminada");
        }
        return localidad;
    }

    private String ajustarTexto(String valor) {
        return valor == null ? null : valor.trim();
    }
}
