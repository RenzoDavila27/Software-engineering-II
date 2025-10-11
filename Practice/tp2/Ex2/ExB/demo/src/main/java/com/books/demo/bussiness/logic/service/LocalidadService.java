package com.books.demo.bussiness.logic.service;

import com.books.demo.bussiness.domain.Localidad;
import com.books.demo.bussiness.persistance.LocalidadRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocalidadService {
    
    @Autowired
    private final LocalidadRepository localidadRepository;

    public LocalidadService(LocalidadRepository localidadRepository) {
        this.localidadRepository = localidadRepository;
    }

    @Transactional
    public Localidad crearLocalidad(Localidad localidad) {
        try {
            validarLocalidad(localidad);
            localidad.setEliminado(false);
            return localidadRepository.save(localidad);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al crear localidad", e);
        }
    }

    @Transactional
    public Localidad modificarLocalidad(Long id, Localidad datosActualizados) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("El id de la localidad no puede ser nulo");
            }
            validarLocalidad(datosActualizados);
            Localidad localidad = localidadRepository.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Localidad no encontrada con id " + id));
            localidad.setDenominacion(datosActualizados.getDenominacion());
            return localidadRepository.save(localidad);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al modificar localidad", e);
        }
    }

    @Transactional
    public void eliminarLocalidad(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("El id de la localidad no puede ser nulo");
            }
            Localidad localidad = localidadRepository.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Localidad no encontrada con id " + id));
            localidad.setEliminado(true);
            localidadRepository.save(localidad);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar localidad", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Localidad> listarActivas() {
        try {
            return localidadRepository.listarLocalidadesActivas();
        } catch (Exception e) {
            throw new RuntimeException("Error al listar localidades", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Localidad> buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id de la localidad no puede ser nulo");
        }
        try {
            return localidadRepository.buscarPorId(id);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar localidad", e);
        }
    }

    private void validarLocalidad(Localidad localidad) {
        if (localidad == null) {
            throw new IllegalArgumentException("La localidad no puede ser nula");
        }
        if (textoInvalido(localidad.getDenominacion())) {
            throw new IllegalArgumentException("La denominacion es obligatoria");
        }
    }

    private boolean textoInvalido(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
