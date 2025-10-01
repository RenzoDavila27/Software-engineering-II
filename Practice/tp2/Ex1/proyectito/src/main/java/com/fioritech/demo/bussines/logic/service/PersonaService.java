package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Persona;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PersonaService {

    @PersistenceContext
    private EntityManager entityManager;

    public Persona crearPersona(Persona persona) {
        verificarAtributos(persona);
        if (persona.getId() != null) {
            throw new BusinessException("La persona ya tiene un id asignado");
        }
        persona.setNombre(persona.getNombre().trim());
        persona.setApellido(persona.getApellido().trim());
        persona.setTelefono(persona.getTelefono().trim());
        persona.setCorreo(persona.getCorreo().trim());
        persona.setEliminado(false);
        entityManager.persist(persona);
        return persona;
    }

    public Persona modificarPersona(Long id, Persona cambios) {
        Persona existente = entityManager.find(Persona.class, id);
        if (existente == null) {
            throw new EntityNotFoundException("No existe la persona con id " + id);
        }
        if (existente.isEliminado()) {
            throw new BusinessException("La persona con id " + id + " esta eliminada");
        }
        verificarAtributos(cambios);
        existente.setNombre(cambios.getNombre().trim());
        existente.setApellido(cambios.getApellido().trim());
        existente.setTelefono(cambios.getTelefono().trim());
        existente.setCorreo(cambios.getCorreo().trim());
        return entityManager.merge(existente);
    }

    public void eliminarPersona(Long id) {
        Persona existente = entityManager.find(Persona.class, id);
        if (existente == null) {
            throw new EntityNotFoundException("No existe la persona con id " + id);
        }
        if (existente.isEliminado()) {
            throw new BusinessException("La persona con id " + id + " ya esta eliminada");
        }
        existente.setEliminado(true);
        entityManager.merge(existente);
    }

    public void verificarAtributos(Persona persona) {
        if (persona == null) {
            throw new BusinessException("La persona es obligatoria");
        }
        if (ValidationUtils.isBlank(persona.getNombre())) {
            throw new BusinessException("El nombre es obligatorio");
        }
        if (ValidationUtils.isBlank(persona.getApellido())) {
            throw new BusinessException("El apellido es obligatorio");
        }
        if (ValidationUtils.isBlank(persona.getTelefono())) {
            throw new BusinessException("El telefono es obligatorio");
        }
        if (ValidationUtils.isBlank(persona.getCorreo())) {
            throw new BusinessException("El correo es obligatorio");
        }
        if (!ValidationUtils.isValidEmail(persona.getCorreo().trim())) {
            throw new BusinessException("El correo tiene un formato invalido");
        }
    }
}
