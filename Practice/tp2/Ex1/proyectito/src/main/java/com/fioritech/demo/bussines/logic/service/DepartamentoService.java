package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Departamento;
import com.fioritech.demo.bussines.domain.Provincia;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import com.fioritech.demo.bussines.repository.DepartamentoRepository;
import com.fioritech.demo.bussines.repository.ProvinciaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;
    private final ProvinciaRepository provinciaRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository,
                               ProvinciaRepository provinciaRepository) {
        this.departamentoRepository = departamentoRepository;
        this.provinciaRepository = provinciaRepository;
    }

    public Departamento crearDepartamento(Departamento departamento) {
        verificarAtributos(departamento);
        if (departamento.getId() != null) {
            throw new BusinessException("El departamento ya tiene un id asignado");
        }
        Provincia provincia = obtenerProvinciaActiva(departamento.getProvincia().getId());
        departamento.setNombre(departamento.getNombre().trim());
        departamento.setProvincia(provincia);
        departamento.setEliminado(false);
        return departamentoRepository.save(departamento);
    }

    public Departamento modificarDepartamento(Long id, Departamento cambios) {
        Departamento existente = obtenerDepartamentoActivo(id);
        verificarAtributos(cambios);
        Provincia provincia = obtenerProvinciaActiva(cambios.getProvincia().getId());
        existente.setNombre(cambios.getNombre().trim());
        existente.setProvincia(provincia);
        return departamentoRepository.save(existente);
    }

    public void eliminarDepartamento(Long id) {
        Departamento existente = obtenerDepartamentoActivo(id);
        existente.setEliminado(true);
        departamentoRepository.save(existente);
    }

    @Transactional(readOnly = true)
    public Collection<Departamento> listarDepartamentos() {
        return departamentoRepository.buscarDepartamentosActivos();
    }

    @Transactional(readOnly = true)
    public Departamento buscarDepartamentoPorId(Long id) {
        return obtenerDepartamentoActivo(id);
    }

    @Transactional(readOnly = true)
    public Departamento obtenerDepartamentoActivo(Long id) {
        Departamento departamento = departamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe el departamento con id " + id));
        if (departamento.isEliminado()) {
            throw new BusinessException("El departamento con id " + id + " esta eliminado");
        }
        return departamento;
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

    private Provincia obtenerProvinciaActiva(Long provinciaId) {
        Provincia provincia = provinciaRepository.findById(provinciaId)
                .orElseThrow(() -> new EntityNotFoundException("No existe la provincia con id " + provinciaId));
        if (provincia.isEliminado()) {
            throw new BusinessException("La provincia con id " + provinciaId + " esta eliminada");
        }
        return provincia;
    }
}
