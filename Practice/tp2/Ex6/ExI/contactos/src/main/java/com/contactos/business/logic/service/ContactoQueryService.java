package com.contactos.business.logic.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.contactos.business.domain.Contacto;
import com.contactos.business.domain.Persona;
import com.contactos.business.logic.error.ErrorServiceException;
import com.contactos.business.persistence.repository.ContactoRepository;
import com.contactos.business.persistence.repository.PersonaRepository;

@Service
public class ContactoQueryService {

    private final ContactoRepository contactoRepository;
    private final PersonaRepository personaRepository;

    public ContactoQueryService(ContactoRepository contactoRepository,
                                PersonaRepository personaRepository) {
        this.contactoRepository = contactoRepository;
        this.personaRepository = personaRepository;
    }

    @Transactional(readOnly = true)
    public List<Contacto> listarPorPersona(Long personaId) throws ErrorServiceException {
        Persona persona = personaRepository.findById(personaId)
                .filter(p -> !Boolean.TRUE.equals(p.isEliminado()))
                .orElseThrow(() -> new ErrorServiceException("La persona indicada no existe"));
        try {
            return contactoRepository.findByPersonaAndEliminadoFalse(persona);
        } catch (Exception e) {
            throw new ErrorServiceException("No fue posible recuperar los contactos vinculados", e);
        }
    }
}
