package com.contactos.business.logic.service;

import com.contactos.business.domain.Contacto;
import com.contactos.business.domain.Persona;
import com.contactos.business.logic.error.ErrorServiceException;
import com.contactos.business.persistence.repository.BaseRepository;
import com.contactos.business.persistence.repository.PersonaRepository;

public abstract class AbstractContactoService<T extends Contacto> extends BaseService<T, Long> {

    private final PersonaRepository personaRepository;

    protected AbstractContactoService(BaseRepository<T, Long> repository,
                                      PersonaRepository personaRepository) {
        super(repository);
        this.personaRepository = personaRepository;
    }

    @Override
    protected void validar(BaseUseCaseService useCase, T entidad) throws ErrorServiceException {
        if (entidad == null) {
            throw new ErrorServiceException("El contacto es requerido");
        }
        if (entidad.getPersona() == null || entidad.getPersona().getId() == null) {
            throw new ErrorServiceException("Debe asociar el contacto a una persona válida");
        }
        if (entidad.getTipoContacto() == null) {
            throw new ErrorServiceException("El tipo de contacto es requerido");
        }
        validarDatosEspecificos(entidad);
    }

    @Override
    protected void preAlta(T entidad) throws ErrorServiceException {
        asociarPersona(entidad);
        entidad.setEliminado(false);
    }

    @Override
    protected void preModificacion(T entidad) throws ErrorServiceException {
        asociarPersona(entidad);
    }

    private void asociarPersona(T entidad) throws ErrorServiceException {
        Persona persona = personaRepository.findById(entidad.getPersona().getId())
                .filter(p -> !Boolean.TRUE.equals(p.isEliminado()))
                .orElseThrow(() -> new ErrorServiceException("La persona asociada no existe o está eliminada"));
        entidad.setPersona(persona);
    }

    protected abstract void validarDatosEspecificos(T entidad) throws ErrorServiceException;
}
