package com.contactos.business.logic.service;

import org.springframework.stereotype.Service;

import com.contactos.business.domain.ContactoTelefonico;
import com.contactos.business.logic.error.ErrorServiceException;
import com.contactos.business.persistence.repository.ContactoTelefonicoRepository;
import com.contactos.business.persistence.repository.PersonaRepository;

@Service
public class ContactoTelefonicoService extends AbstractContactoService<ContactoTelefonico> {

    public ContactoTelefonicoService(ContactoTelefonicoRepository repository,
                                     PersonaRepository personaRepository) {
        super(repository, personaRepository);
    }

    @Override
    protected void validarDatosEspecificos(ContactoTelefonico entidad) throws ErrorServiceException {
        if (entidad.getTelefono() == null || entidad.getTelefono().isBlank()) {
            throw new ErrorServiceException("El número de teléfono es requerido");
        }
        if (entidad.getTipoTelefono() == null) {
            throw new ErrorServiceException("El tipo de teléfono es requerido");
        }
    }
}
