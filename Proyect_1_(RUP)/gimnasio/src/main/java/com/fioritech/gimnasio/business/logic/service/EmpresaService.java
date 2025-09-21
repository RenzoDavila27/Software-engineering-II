package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.Empresa;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.EmpresaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public Empresa crearEmpresa(String nombre, String telefono, String correoElectronico) {
        validar(nombre);
        Empresa empresa = new Empresa();
        empresa.setNombre(nombre.trim());
        empresa.setTelefono(telefono);
        empresa.setCorreoElectronico(correoElectronico);
        return empresaRepository.save(empresa);
    }

    public void validar(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new BusinessException("El nombre de la empresa es obligatorio");
        }
        String normalized = nombre.trim().toLowerCase(Locale.ROOT);
        boolean existe = empresaRepository.findAll().stream()
            .filter(e -> !e.isEliminado())
            .map(e -> e.getNombre().trim().toLowerCase(Locale.ROOT))
            .anyMatch(normalized::equals);
        if (existe) {
            throw new BusinessException("Ya existe una empresa con ese nombre");
        }
    }

    @Transactional(readOnly = true)
    public Empresa buscarEmpresa(String id) {
        return empresaRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Empresa no encontrada"));
    }

    @Transactional(readOnly = true)
    public Empresa buscarEmpresaPorNombre(String nombre) {
        String normalized = nombre == null ? "" : nombre.trim().toLowerCase(Locale.ROOT);
        return empresaRepository.findAll().stream()
            .filter(e -> !e.isEliminado())
            .filter(e -> e.getNombre().trim().toLowerCase(Locale.ROOT).equals(normalized))
            .findFirst()
            .orElseThrow(() -> new BusinessException("Empresa no encontrada"));
    }

    public Empresa modificarEmpresa(String id, String nombre, String telefono, String correoElectronico) {
        Empresa empresa = buscarEmpresa(id);
        if (empresa.isEliminado()) {
            throw new BusinessException("No se puede modificar una empresa eliminada");
        }
        if (nombre != null && !nombre.isBlank() && !empresa.getNombre().equalsIgnoreCase(nombre.trim())) {
            validar(nombre);
            empresa.setNombre(nombre.trim());
        }
        if (telefono != null) {
            empresa.setTelefono(telefono);
        }
        if (correoElectronico != null) {
            empresa.setCorreoElectronico(correoElectronico);
        }
        return empresaRepository.save(empresa);
    }

    public void eliminarEmpresa(String id) {
        Empresa empresa = buscarEmpresa(id);
        empresa.setEliminado(true);
        empresaRepository.save(empresa);
    }

    @Transactional(readOnly = true)
    public List<Empresa> listarEmpresa() {
        return empresaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Collection<Empresa> listarEmpresaActiva() {
        Collection<Empresa> empresas = empresaRepository.findAll();
        return empresas;
            
    }
}
