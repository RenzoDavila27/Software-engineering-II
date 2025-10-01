package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Proveedor;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import com.fioritech.demo.bussines.repository.ProveedorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
public class ProveedorService {

    private final PersonaService personaService;
    private final ProveedorRepository proveedorRepository;

    public ProveedorService(PersonaService personaService, ProveedorRepository proveedorRepository) {
        this.personaService = personaService;
        this.proveedorRepository = proveedorRepository;
    }

    public Proveedor crearProveedor(Proveedor proveedor) {
        verificarAtributos(proveedor);
        if (proveedor.getId() != null) {
            throw new BusinessException("El proveedor ya tiene un id asignado");
        }
        proveedor.setNombre(proveedor.getNombre().trim());
        proveedor.setApellido(proveedor.getApellido().trim());
        proveedor.setTelefono(proveedor.getTelefono().trim());
        proveedor.setCorreo(proveedor.getCorreo().trim());
        proveedor.setCuit(proveedor.getCuit().trim());
        proveedor.setEliminado(false);
        return proveedorRepository.save(proveedor);
    }

    public Proveedor modificarProveedor(Long id, Proveedor cambios) {
        Proveedor existente = obtenerProveedorActivo(id);
        verificarAtributos(cambios);
        existente.setNombre(cambios.getNombre().trim());
        existente.setApellido(cambios.getApellido().trim());
        existente.setTelefono(cambios.getTelefono().trim());
        existente.setCorreo(cambios.getCorreo().trim());
        existente.setCuit(cambios.getCuit().trim());
        return proveedorRepository.save(existente);
    }

    public void eliminarProveedor(Long id) {
        Proveedor existente = obtenerProveedorActivo(id);
        existente.setEliminado(true);
        proveedorRepository.save(existente);
    }

    @Transactional(readOnly = true)
    public Collection<Proveedor> listarProveedores() {
        return proveedorRepository.buscarProveedoresActivos();
    }

    @Transactional(readOnly = true)
    public Proveedor buscarProveedorPorId(Long id) {
        return obtenerProveedorActivo(id);
    }

    public void verificarAtributos(Proveedor proveedor) {
        personaService.verificarAtributos(proveedor);
        if (ValidationUtils.isBlank(proveedor.getCuit())) {
            throw new BusinessException("El CUIT es obligatorio");
        }
    }

    private Proveedor obtenerProveedorActivo(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe el proveedor con id " + id));
        if (proveedor.isEliminado()) {
            throw new BusinessException("El proveedor con id " + id + " esta eliminado");
        }
        return proveedor;
    }
}
