package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.Departamento;
import com.fioritech.gimnasio.business.domain.Localidad;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.LocalidadRepository;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LocalidadService {

    private final LocalidadRepository localidadRepository;
    private final DepartamentoService departamentoService;

    public LocalidadService(LocalidadRepository localidadRepository, DepartamentoService departamentoService) {
        this.localidadRepository = localidadRepository;
        this.departamentoService = departamentoService;
    }

    public Localidad crearLocalidad(String nombre, String codigoPostal, String idDepartamento) {
        Departamento departamento = departamentoService.buscarDepartamento(idDepartamento);
        validar(nombre, codigoPostal, departamento);
        Localidad localidad = new Localidad();
        localidad.setNombre(nombre.trim());
        localidad.setCodigoPostal(codigoPostal.trim());
        localidad.setDepartamento(departamento);
        return localidadRepository.save(localidad);
    }

    public void validar(String nombre, String codigoPostal, Departamento departamento) {
        if (nombre == null || nombre.isBlank()) {
            throw new BusinessException("El nombre de la localidad es obligatorio");
        }
        if (codigoPostal == null || codigoPostal.isBlank()) {
            throw new BusinessException("El codigo postal es obligatorio");
        }
        if (departamento == null || departamento.isEliminado()) {
            throw new BusinessException("El departamento asociado es obligatorio");
        }
        String normalized = nombre.trim().toLowerCase(Locale.ROOT);
        boolean existe = localidadRepository.findAll().stream()
            .filter(l -> !l.isEliminado())
            .filter(l -> l.getDepartamento().getId().equals(departamento.getId()))
            .map(l -> l.getNombre().trim().toLowerCase(Locale.ROOT))
            .anyMatch(normalized::equals);
        if (existe) {
            throw new BusinessException("Ya existe una localidad con ese nombre en el departamento seleccionado");
        }
    }

    @Transactional(readOnly = true)
    public Localidad buscarLocalidad(String id) {
        return localidadRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Localidad no encontrada"));
    }

    @Transactional(readOnly = true)
    public Localidad buscarLocalidadPorNombre(String nombre) {
        String normalized = nombre == null ? "" : nombre.trim().toLowerCase(Locale.ROOT);
        return localidadRepository.findAll().stream()
            .filter(l -> !l.isEliminado())
            .filter(l -> l.getNombre().trim().toLowerCase(Locale.ROOT).equals(normalized))
            .findFirst()
            .orElseThrow(() -> new BusinessException("Localidad no encontrada"));
    }

    @Transactional(readOnly = true)
    public Localidad buscarLocalidadPorCodigoPostal(String codigoPostal) {
        String normalized = codigoPostal == null ? "" : codigoPostal.trim();
        return localidadRepository.findAll().stream()
            .filter(l -> !l.isEliminado())
            .filter(l -> l.getCodigoPostal().equalsIgnoreCase(normalized))
            .findFirst()
            .orElseThrow(() -> new BusinessException("Localidad no encontrada"));
    }

    public Localidad modificarLocalidad(String id, String nombre, String codigoPostal, String idDepartamento) {
        Localidad localidad = buscarLocalidad(id);
        Departamento departamento = localidad.getDepartamento();
        if (idDepartamento != null && !idDepartamento.isBlank() && (departamento == null || !departamento.getId().equals(idDepartamento))) {
            departamento = departamentoService.buscarDepartamento(idDepartamento);
        }
        if (nombre != null && !nombre.isBlank() && !localidad.getNombre().equalsIgnoreCase(nombre.trim())) {
            validar(nombre, codigoPostal != null ? codigoPostal : localidad.getCodigoPostal(), departamento);
            localidad.setNombre(nombre.trim());
        }
        if (codigoPostal != null && !codigoPostal.isBlank()) {
            localidad.setCodigoPostal(codigoPostal.trim());
        }
        localidad.setDepartamento(departamento);
        return localidadRepository.save(localidad);
    }

    public void eliminarLocalidad(String id) {
        Localidad localidad = buscarLocalidad(id);
        localidad.setEliminado(true);
        localidadRepository.save(localidad);
    }

    @Transactional(readOnly = true)
    public List<Localidad> listarLocalidad(String idDepartamento) {
        return localidadRepository.findAll().stream()
            .filter(l -> idDepartamento == null || idDepartamento.isBlank() || l.getDepartamento().getId().equals(idDepartamento))
            .toList();
    }

    @Transactional(readOnly = true)
    public Collection<Localidad> listarLocalidadActivo() {
        return localidadRepository.listarLocalidadActivo();
    }
}
