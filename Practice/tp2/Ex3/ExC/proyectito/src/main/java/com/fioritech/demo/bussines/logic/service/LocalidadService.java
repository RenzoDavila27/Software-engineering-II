package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Departamento;
import com.fioritech.demo.bussines.domain.Localidad;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.service.template.CrudTemplateService;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import com.fioritech.demo.bussines.repository.DepartamentoRepository;
import com.fioritech.demo.bussines.repository.LocalidadRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
public class LocalidadService extends CrudTemplateService<Localidad, Long> {

    private final LocalidadRepository localidadRepository;
    private final DepartamentoRepository departamentoRepository;

    public LocalidadService(LocalidadRepository localidadRepository,
                            DepartamentoRepository departamentoRepository) {
        this.localidadRepository = localidadRepository;
        this.departamentoRepository = departamentoRepository;
    }

    public Localidad crearLocalidad(Localidad localidad) {
        return crearEntidad(localidad);
    }

    public Localidad modificarLocalidad(Long id, Localidad cambios) {
        return modificarEntidad(id, cambios);
    }

    public void eliminarLocalidad(Long id) {
        eliminarEntidad(id);
    }

    @Transactional(readOnly = true)
    public Collection<Localidad> listarLocalidades() {
        return listarEntidades();
    }

    @Transactional(readOnly = true)
    public Localidad buscarLocalidadPorId(Long id) {
        return buscarEntidad(id);
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

    @Override
    protected void validarEntidad(Localidad localidad) {
        verificarAtributos(localidad);
    }

    @Override
    protected void validarEntidadNueva(Localidad localidad) {
        if (localidad.getId() != null) {
            throw new BusinessException("La localidad ya tiene un id asignado");
        }
    }

    @Override
    protected void antesDeCrear(Localidad localidad) {
        Departamento departamento = obtenerDepartamentoActivo(localidad.getDepartamento().getId());
        normalizar(localidad);
        localidad.setDepartamento(departamento);
        localidad.setEliminado(false);
    }

    @Override
    protected void antesDeModificar(Localidad existente, Localidad cambios) {
        Departamento departamento = obtenerDepartamentoActivo(cambios.getDepartamento().getId());
        existente.setDepartamento(departamento);
    }

    @Override
    protected void aplicarCambios(Localidad existente, Localidad cambios) {
        normalizar(cambios);
        existente.setNombre(cambios.getNombre());
        existente.setCodPostal(cambios.getCodPostal());
    }

    @Override
    protected void marcarEliminado(Localidad localidad) {
        localidad.setEliminado(true);
    }

    @Override
    protected Localidad guardar(Localidad localidad) {
        return localidadRepository.save(localidad);
    }

    @Override
    protected Localidad obtenerPorId(Long id) {
        return obtenerLocalidadActiva(id);
    }

    @Override
    protected Collection<Localidad> obtenerListado() {
        return localidadRepository.buscarLocalidadesActivas();
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

    private void normalizar(Localidad localidad) {
        localidad.setNombre(localidad.getNombre().trim());
        localidad.setCodPostal(localidad.getCodPostal().trim());
    }
}
