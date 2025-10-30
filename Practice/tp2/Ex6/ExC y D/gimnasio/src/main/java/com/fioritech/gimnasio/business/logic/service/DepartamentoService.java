package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.Departamento;
import com.fioritech.gimnasio.business.domain.Provincia;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.DepartamentoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;
    
    private final ProvinciaService provinciaService;

    public DepartamentoService(DepartamentoRepository departamentoRepository, ProvinciaService provinciaService) {
        this.departamentoRepository = departamentoRepository;
        this.provinciaService = provinciaService;
    }

    public Departamento crearDepartamento(String nombre, String idProvincia) {
        Provincia provincia = provinciaService.buscarProvincia(idProvincia);
        validar(nombre, provincia);
        Departamento departamento = new Departamento();
        departamento.setNombre(nombre.trim());
        departamento.setProvincia(provincia);
        return departamentoRepository.save(departamento);
    }

    public void validar(String nombre, Provincia provincia) {
        if (nombre == null || nombre.isBlank()) {
            throw new BusinessException("El nombre del departamento es obligatorio");
        }
        if (provincia == null || provincia.isEliminado()) {
            throw new BusinessException("La provincia asociada es obligatoria");
        }
        String normalized = nombre.trim().toLowerCase(Locale.ROOT);
        boolean existe = departamentoRepository.findAll().stream()
            .filter(d -> !d.isEliminado())
            .filter(d -> d.getProvincia().getId().equals(provincia.getId()))
            .map(d -> d.getNombre().trim().toLowerCase(Locale.ROOT))
            .anyMatch(normalized::equals);
        if (existe) {
            throw new BusinessException("Ya existe un departamento con ese nombre en la provincia seleccionada");
        }
    }

    @Transactional(readOnly = true)
    public Departamento buscarDepartamento(String id) {
        return departamentoRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Departamento no encontrado"));
    }

    @Transactional(readOnly = true)
    public Departamento buscarDepartamentoPorNombre(String nombre) {
        String normalized = nombre == null ? "" : nombre.trim().toLowerCase(Locale.ROOT);
        return departamentoRepository.findAll().stream()
            .filter(d -> !d.isEliminado())
            .filter(d -> d.getNombre().trim().toLowerCase(Locale.ROOT).equals(normalized))
            .findFirst()
            .orElseThrow(() -> new BusinessException("Departamento no encontrado"));
    }

    public Departamento modificarDepartamento(String id, String nombre, String idProvincia) {
        Departamento departamento = buscarDepartamento(id);
        Provincia provincia = departamento.getProvincia();
        if (idProvincia != null && !idProvincia.isBlank() && (provincia == null || !provincia.getId().equals(idProvincia))) {
            provincia = provinciaService.buscarProvincia(idProvincia);
        }
        if (nombre != null && !nombre.isBlank() && !departamento.getNombre().equalsIgnoreCase(nombre.trim())) {
            validar(nombre, provincia);
            departamento.setNombre(nombre.trim());
        }
        departamento.setProvincia(provincia);
        return departamentoRepository.save(departamento);
    }

    public void eliminarDepartamento(String id) {
        Departamento departamento = buscarDepartamento(id);
        departamento.setEliminado(true);
        departamentoRepository.save(departamento);
    }

    @Transactional(readOnly = true)
    public List<Departamento> listarDepartamento(String idProvincia) {
        return departamentoRepository.findAll().stream()
            .filter(d -> idProvincia == null || idProvincia.isBlank() || d.getProvincia().getId().equals(idProvincia))
            .toList();
    }

    @Transactional(readOnly = true)
    public Collection<Departamento> listarDepartamentoActivo() {
        return departamentoRepository.listarDepartamentoActivo();
          
    }
}
