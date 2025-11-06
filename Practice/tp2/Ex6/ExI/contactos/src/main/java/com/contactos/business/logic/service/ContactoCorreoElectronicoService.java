package com.contactos.business.logic.service;

import org.springframework.stereotype.Service;

import com.contactos.business.domain.ContactoCorreoElectronico;
import com.contactos.business.logic.error.ErrorServiceException;
import com.contactos.business.persistence.repository.ContactoCorreoElectronicoRepository;
import com.contactos.business.persistence.repository.PersonaRepository;

@Service
public class ContactoCorreoElectronicoService extends AbstractContactoService<ContactoCorreoElectronico> {

    public ContactoCorreoElectronicoService(ContactoCorreoElectronicoRepository repository,
                                            PersonaRepository personaRepository) {
        super(repository, personaRepository);
    }

    @Override
    protected void validarDatosEspecificos(ContactoCorreoElectronico entidad) throws ErrorServiceException {
        if (entidad.getEmail() == null || entidad.getEmail().isBlank()) {
            throw new ErrorServiceException("El email del contacto es requerido");
        }
    }
}
