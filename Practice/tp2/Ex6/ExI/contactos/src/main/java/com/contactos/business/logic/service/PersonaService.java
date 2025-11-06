package com.contactos.business.logic.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.contactos.business.domain.Persona;
import com.contactos.business.logic.error.ErrorServiceException;
import com.contactos.business.persistence.repository.PersonaRepository;

@Service
public class PersonaService extends BaseService<Persona, Long> {

    private final PersonaRepository personaRepository;

    public PersonaService(PersonaRepository personaRepository) {
        super(personaRepository);
        this.personaRepository = personaRepository;
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Persona entidad) throws ErrorServiceException {
        if (entidad == null) {
            throw new ErrorServiceException("La persona es requerida");
        }
        if (entidad.getNombre() == null || entidad.getNombre().isBlank()) {
            throw new ErrorServiceException("El nombre de la persona es requerido");
        }
        if (entidad.getApellido() == null || entidad.getApellido().isBlank()) {
            throw new ErrorServiceException("El apellido de la persona es requerido");
        }
    }

    @Transactional(readOnly = true)
    public Optional<Persona> obtenerConRelaciones(Long id) throws ErrorServiceException {
        try {
            return personaRepository.findWithRelationshipsById(id)
                    .filter(persona -> !Boolean.TRUE.equals(persona.isEliminado()));
        } catch (Exception e) {
            throw new ErrorServiceException("No fue posible recuperar la persona solicitada", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Persona> listarActivasConRelaciones() throws ErrorServiceException {
        try {
            return personaRepository.findAllByEliminadoFalse();
        } catch (Exception e) {
            throw new ErrorServiceException("No fue posible listar las personas", e);
        }
    }
}
