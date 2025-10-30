package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.Direccion;
import com.fioritech.gimnasio.business.domain.Localidad;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.DireccionRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DireccionService {

    private final DireccionRepository direccionRepository;
    private final LocalidadService localidadService;

    public DireccionService(DireccionRepository direccionRepository, LocalidadService localidadService) {
        this.direccionRepository = direccionRepository;
        this.localidadService = localidadService;
    }

    @Transactional
    public Direccion crearDireccion(String calle, String numeracion, String barrio, String manzanaPiso,
        String casaDepartamento, String referencia, String idLocalidad) {
        System.out.println("LLEGUE A CREAR DIRECCION");
        Localidad localidad = localidadService.buscarLocalidad(idLocalidad);
        System.out.println("BUSQUE LA LOCALIDAD");
        validar(calle, numeracion, barrio, manzanaPiso, casaDepartamento, referencia, localidad);
        Direccion direccion = new Direccion();
        direccion.setCalle(calle.trim());
        direccion.setNumeracion(numeracion.trim());
        direccion.setBarrio(barrio);
        direccion.setManzanaPiso(manzanaPiso);
        direccion.setCasaDepartamento(casaDepartamento);
        direccion.setReferencia(referencia);
        direccion.setLocalidad(localidad);
        return direccionRepository.save(direccion);
    }

    public void validar(String calle, String numeracion, String barrio, String manzanaPiso,
        String casaDepartamento, String referencia, Localidad localidad) {
        if (calle == null || calle.isBlank()) {
            throw new BusinessException("La calle es obligatoria");
        }
        if (numeracion == null || numeracion.isBlank()) {
            throw new BusinessException("La numeracion es obligatoria");
        }
        if (localidad == null || localidad.isEliminado()) {
            throw new BusinessException("La localidad es obligatoria");
        }
        String calleNorm = calle.trim().toLowerCase(Locale.ROOT);
        String numeroNorm = numeracion.trim().toLowerCase(Locale.ROOT);
        boolean existe = direccionRepository.findAll().stream()
            .filter(d -> !d.isEliminado())
            .filter(d -> d.getLocalidad().getId().equals(localidad.getId()))
            .anyMatch(d -> d.getCalle().trim().toLowerCase(Locale.ROOT).equals(calleNorm)
                && d.getNumeracion().trim().toLowerCase(Locale.ROOT).equals(numeroNorm));
        if (existe) {
            throw new BusinessException("Ya existe una direccion con esa calle y numeracion en la localidad");
        }
    }

    @Transactional(readOnly = true)
    public Direccion buscarDireccion(String id) {
        return direccionRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Direccion no encontrada"));
    }

    @Transactional(readOnly = true)
    public Direccion buscarDireccionPorCalleNumeracion(String calle, String numeracion) {
        String calleNorm = calle == null ? "" : calle.trim().toLowerCase(Locale.ROOT);
        String numeroNorm = numeracion == null ? "" : numeracion.trim().toLowerCase(Locale.ROOT);
        return direccionRepository.findAll().stream()
            .filter(d -> !d.isEliminado())
            .filter(d -> d.getCalle().trim().toLowerCase(Locale.ROOT).equals(calleNorm)
                && d.getNumeracion().trim().toLowerCase(Locale.ROOT).equals(numeroNorm))
            .findFirst()
            .orElseThrow(() -> new BusinessException("Direccion no encontrada"));
    }

    public Direccion modificarDireccion(String id, String calle, String numeracion, String barrio,
        String manzanaPiso, String casaDepartamento, String referencia, String idLocalidad) {
        Direccion direccion = buscarDireccion(id);
        Localidad localidad = direccion.getLocalidad();
        if (idLocalidad != null && !idLocalidad.isBlank() && (localidad == null || !localidad.getId().equals(idLocalidad))) {
            localidad = localidadService.buscarLocalidad(idLocalidad);
        }
        if (calle != null && !calle.isBlank()) {
            direccion.setCalle(calle.trim());
        }
        if (numeracion != null && !numeracion.isBlank()) {
            direccion.setNumeracion(numeracion.trim());
        }
        direccion.setBarrio(barrio);
        direccion.setManzanaPiso(manzanaPiso);
        direccion.setCasaDepartamento(casaDepartamento);
        direccion.setReferencia(referencia);
        direccion.setLocalidad(localidad);
        return direccionRepository.save(direccion);
    }

    public void eliminarDireccion(String id) {
        Direccion direccion = buscarDireccion(id);
        direccion.setEliminado(true);
        direccionRepository.save(direccion);
    }

    @Transactional(readOnly = true)
    public List<Direccion> listarDirecciones(String idLocalidad) {
        return direccionRepository.findAll().stream()
            .filter(d -> idLocalidad == null || idLocalidad.isBlank() || d.getLocalidad().getId().equals(idLocalidad))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<Direccion> listarDireccionesActivas(String idLocalidad) {
        return direccionRepository.findAll().stream()
            .filter(d -> !d.isEliminado())
            .filter(d -> idLocalidad == null || idLocalidad.isBlank() || d.getLocalidad().getId().equals(idLocalidad))
            .toList();
    }
}
