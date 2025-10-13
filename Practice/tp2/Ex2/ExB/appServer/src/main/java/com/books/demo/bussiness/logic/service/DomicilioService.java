package com.books.demo.bussiness.logic.service;

import com.books.demo.bussiness.domain.Domicilio;
import com.books.demo.bussiness.domain.Localidad;
import com.books.demo.bussiness.persistance.DomicilioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DomicilioService {

    @Autowired
    private final DomicilioRepository domicilioRepository;
    
    public DomicilioService(DomicilioRepository domicilioRepository) {
        this.domicilioRepository = domicilioRepository;
    }

    @Transactional
    public Domicilio crearDomicilio(Domicilio domicilio) {
        try {
            validarDomicilio(domicilio);
            domicilio.setEliminado(false);
            return domicilioRepository.save(domicilio);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al crear domicilio", e);
        }
    }

    @Transactional
    public Domicilio modificarDomicilio(Long id, Domicilio datosActualizados) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("El id del domicilio no puede ser nulo");
            }
            validarDomicilio(datosActualizados);
            Domicilio domicilio = domicilioRepository.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Domicilio no encontrado con id " + id));
            domicilio.setCalle(datosActualizados.getCalle());
            domicilio.setNumero(datosActualizados.getNumero());
            domicilio.setLocalidad(datosActualizados.getLocalidad());
            domicilio.setEliminado(datosActualizados.isEliminado());
            return domicilioRepository.save(domicilio);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al modificar domicilio", e);
        }
    }

    @Transactional
    public void eliminarDomicilio(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("El id del domicilio no puede ser nulo");
            }
            Domicilio domicilio = domicilioRepository.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Domicilio no encontrado con id " + id));
            domicilio.setEliminado(true);
            domicilioRepository.save(domicilio);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar domicilio", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Domicilio> listarActivos() {
        try {
            return domicilioRepository.listarDomiciliosActivos();
        } catch (Exception e) {
            throw new RuntimeException("Error al listar domicilios", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Domicilio> buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del domicilio no puede ser nulo");
        }
        try {
            return domicilioRepository.buscarPorId(id);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar domicilio", e);
        }
    }

    private void validarDomicilio(Domicilio domicilio) {
        if (domicilio == null) {
            throw new IllegalArgumentException("El domicilio no puede ser nulo");
        }
        if (textoInvalido(domicilio.getCalle())) {
            throw new IllegalArgumentException("La calle es obligatoria");
        }
        if (domicilio.getNumero() == null) {
            throw new IllegalArgumentException("El numero es obligatorio");
        }
        Localidad localidad = domicilio.getLocalidad();
        if (localidad == null || localidad.getId() == null) {
            throw new IllegalArgumentException("La localidad es obligatoria");
        }
    }

    private boolean textoInvalido(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
