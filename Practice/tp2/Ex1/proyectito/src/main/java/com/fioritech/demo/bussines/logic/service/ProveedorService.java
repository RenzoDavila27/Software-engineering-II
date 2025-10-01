package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Proveedor;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProveedorService {

    private final PersonaService personaService;

    @PersistenceContext
    private EntityManager entityManager;

    public ProveedorService(PersonaService personaService) {
        this.personaService = personaService;
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
        entityManager.persist(proveedor);
        return proveedor;
    }

    public Proveedor modificarProveedor(Long id, Proveedor cambios) {
        Proveedor existente = entityManager.find(Proveedor.class, id);
        if (existente == null) {
            throw new EntityNotFoundException("No existe el proveedor con id " + id);
        }
        if (existente.isEliminado()) {
            throw new BusinessException("El proveedor con id " + id + " esta eliminado");
        }
        verificarAtributos(cambios);
        existente.setNombre(cambios.getNombre().trim());
        existente.setApellido(cambios.getApellido().trim());
        existente.setTelefono(cambios.getTelefono().trim());
        existente.setCorreo(cambios.getCorreo().trim());
        existente.setCuit(cambios.getCuit().trim());
        return entityManager.merge(existente);
    }

    public void eliminarProveedor(Long id) {
        Proveedor existente = entityManager.find(Proveedor.class, id);
        if (existente == null) {
            throw new EntityNotFoundException("No existe el proveedor con id " + id);
        }
        if (existente.isEliminado()) {
            throw new BusinessException("El proveedor con id " + id + " ya esta eliminado");
        }
        existente.setEliminado(true);
        entityManager.merge(existente);
    }

    public void verificarAtributos(Proveedor proveedor) {
        personaService.verificarAtributos(proveedor);
        if (ValidationUtils.isBlank(proveedor.getCuit())) {
            throw new BusinessException("El CUIT es obligatorio");
        }
    }
}
