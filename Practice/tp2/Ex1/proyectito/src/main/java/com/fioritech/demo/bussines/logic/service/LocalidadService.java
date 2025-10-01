package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Departamento;
import com.fioritech.demo.bussines.domain.Localidad;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import com.fioritech.demo.bussines.repository.DepartamentoRepository;
import com.fioritech.demo.bussines.repository.LocalidadRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
public class LocalidadService {

    private final LocalidadRepository localidadRepository;
    private final DepartamentoRepository departamentoRepository;

    public LocalidadService(LocalidadRepository localidadRepository,
                            DepartamentoRepository departamentoRepository) {
        this.localidadRepository = localidadRepository;
        this.departamentoRepository = departamentoRepository;
    }

    public Localidad crearLocalidad(Localidad localidad) {
        verificarAtributos(localidad);
        if (localidad.getId() != null) {
            throw new BusinessException("La localidad ya tiene un id asignado");
        }
        Departamento departamento = obtenerDepartamentoActivo(localidad.getDepartamento().getId());
        localidad.setNombre(localidad.getNombre().trim());
        localidad.setCodPostal(localidad.getCodPostal().trim());
        localidad.setDepartamento(departamento);
        localidad.setEliminado(false);
        return localidadRepository.save(localidad);
    }

    public Localidad modificarLocalidad(Long id, Localidad cambios) {
        Localidad existente = obtenerLocalidadActiva(id);
        verificarAtributos(cambios);
        Departamento departamento = obtenerDepartamentoActivo(cambios.getDepartamento().getId());
        existente.setNombre(cambios.getNombre().trim());
        existente.setCodPostal(cambios.getCodPostal().trim());
        existente.setDepartamento(departamento);
        return localidadRepository.save(existente);
    }

    public void eliminarLocalidad(Long id) {
        Localidad existente = obtenerLocalidadActiva(id);
        existente.setEliminado(true);
        localidadRepository.save(existente);
    }

    @Transactional(readOnly = true)
    public Collection<Localidad> listarLocalidades() {
        return localidadRepository.buscarLocalidadesActivas();
    }

    @Transactional(readOnly = true)
    public Localidad buscarLocalidadPorId(Long id) {
        return obtenerLocalidadActiva(id);
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

    private Localidad obtenerLocalidadActiva(Long id) {
        Localidad localidad = localidadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe la localidad con id " + id));
        if (localidad.isEliminado()) {
            throw new BusinessException("La localidad con id " + id + " esta eliminada");
        }
        return localidad;
    }

    private Departamento obtenerDepartamentoActivo(Long departamentoId) {
        Departamento departamento = departamentoRepository.findById(departamentoId)
                .orElseThrow(() -> new EntityNotFoundException("No existe el departamento con id " + departamentoId));
        if (departamento.isEliminado()) {
            throw new BusinessException("El departamento con id " + departamentoId + " esta eliminado");
        }
        return departamento;
    }
}
