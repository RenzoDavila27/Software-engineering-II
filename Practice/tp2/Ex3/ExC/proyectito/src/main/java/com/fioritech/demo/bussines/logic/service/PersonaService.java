package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Persona;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.service.template.CrudTemplateService;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
public class PersonaService extends CrudTemplateService<Persona, Long> {

    @PersistenceContext
    private EntityManager entityManager;

    public Persona crearPersona(Persona persona) {
        return crearEntidad(persona);
    }

    public Persona modificarPersona(Long id, Persona cambios) {
        return modificarEntidad(id, cambios);
    }

    public void eliminarPersona(Long id) {
        eliminarEntidad(id);
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

    @Override
    protected void validarEntidad(Persona persona) {
        verificarAtributos(persona);
    }

    @Override
    protected void validarEntidadNueva(Persona persona) {
        if (persona.getId() != null) {
            throw new BusinessException("La persona ya tiene un id asignado");
        }
    }

    @Override
    protected void antesDeCrear(Persona persona) {
        normalizar(persona);
        persona.setEliminado(false);
    }

    @Override
    protected void aplicarCambios(Persona existente, Persona cambios) {
        normalizar(cambios);
        existente.setNombre(cambios.getNombre());
        existente.setApellido(cambios.getApellido());
        existente.setTelefono(cambios.getTelefono());
        existente.setCorreo(cambios.getCorreo());
    }

    @Override
    protected void marcarEliminado(Persona persona) {
        persona.setEliminado(true);
    }

    @Override
    protected Persona guardar(Persona persona) {
        if (persona.getId() == null) {
            entityManager.persist(persona);
            return persona;
        }
        return entityManager.merge(persona);
    }

    @Override
    protected Persona obtenerPorId(Long id) {
        Persona persona = entityManager.find(Persona.class, id);
        if (persona == null) {
            throw new EntityNotFoundException("No existe la persona con id " + id);
        }
        if (persona.isEliminado()) {
            throw new BusinessException("La persona con id " + id + " esta eliminada");
        }
        return persona;
    }

    @Override
    protected Collection<Persona> obtenerListado() {
        return entityManager.createQuery(
                        "SELECT p FROM Persona p WHERE p.eliminado = false",
                        Persona.class)
                .getResultList();
    }

    private void normalizar(Persona persona) {
        persona.setNombre(persona.getNombre().trim());
        persona.setApellido(persona.getApellido().trim());
        persona.setTelefono(persona.getTelefono().trim());
        persona.setCorreo(persona.getCorreo().trim());
    }
}

