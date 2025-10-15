package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Departamento;
import com.fioritech.demo.bussines.domain.Provincia;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.service.template.CrudTemplateService;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import com.fioritech.demo.bussines.repository.DepartamentoRepository;
import com.fioritech.demo.bussines.repository.ProvinciaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
public class DepartamentoService extends CrudTemplateService<Departamento, Long> {

    private final DepartamentoRepository departamentoRepository;
    private final ProvinciaRepository provinciaRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository,
                               ProvinciaRepository provinciaRepository) {
        this.departamentoRepository = departamentoRepository;
        this.provinciaRepository = provinciaRepository;
    }

    public Departamento crearDepartamento(Departamento departamento) {
        return crearEntidad(departamento);
    }

    public Departamento modificarDepartamento(Long id, Departamento cambios) {
        return modificarEntidad(id, cambios);
    }

    public void eliminarDepartamento(Long id) {
        eliminarEntidad(id);
    }

    @Transactional(readOnly = true)
    public Collection<Departamento> listarDepartamentos() {
        return listarEntidades();
    }

    @Transactional(readOnly = true)
    public Departamento buscarDepartamentoPorId(Long id) {
        return buscarEntidad(id);
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

    @Override
    protected void validarEntidad(Departamento departamento) {
        verificarAtributos(departamento);
    }

    @Override
    protected void validarEntidadNueva(Departamento departamento) {
        if (departamento.getId() != null) {
            throw new BusinessException("El departamento ya tiene un id asignado");
        }
    }

    @Override
    protected void antesDeCrear(Departamento departamento) {
        Provincia provincia = obtenerProvinciaActiva(departamento.getProvincia().getId());
        departamento.setNombre(departamento.getNombre().trim());
        departamento.setProvincia(provincia);
        departamento.setEliminado(false);
    }

    @Override
    protected void antesDeModificar(Departamento existente, Departamento cambios) {
        Provincia provincia = obtenerProvinciaActiva(cambios.getProvincia().getId());
        existente.setProvincia(provincia);
    }

    @Override
    protected void aplicarCambios(Departamento existente, Departamento cambios) {
        existente.setNombre(cambios.getNombre().trim());
    }

    @Override
    protected void marcarEliminado(Departamento departamento) {
        departamento.setEliminado(true);
    }

    @Override
    protected Departamento guardar(Departamento departamento) {
        return departamentoRepository.save(departamento);
    }

    @Override
    protected Departamento obtenerPorId(Long id) {
        return obtenerDepartamentoActivo(id);
    }

    @Override
    protected Collection<Departamento> obtenerListado() {
        return departamentoRepository.buscarDepartamentosActivos();
    }

    private Departamento obtenerDepartamentoActivo(Long id) {
        Departamento departamento = departamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe el departamento con id " + id));
        if (departamento.isEliminado()) {
            throw new BusinessException("El departamento con id " + id + " esta eliminado");
        }
        return departamento;
    }

    private Provincia obtenerProvinciaActiva(Long provinciaId) {
        Provincia provincia = provinciaRepository.findById(provinciaId)
                .orElseThrow(() -> new EntityNotFoundException("No existe la provincia con id " + provinciaId));
        if (provincia.isEliminado()) {
            throw new BusinessException("La provincia con id " + provinciaId + " esta eliminada");
        }
        return provincia;
    }
}

