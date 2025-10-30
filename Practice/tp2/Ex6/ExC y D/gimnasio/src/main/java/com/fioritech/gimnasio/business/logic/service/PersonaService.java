package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.Persona;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.PersonaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PersonaService {

    private final PersonaRepository personaRepository;

    public PersonaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    @Transactional(readOnly = true)
    public Persona buscarPersona(String id) {
        return personaRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Persona no encontrada"));
    }

    public void eliminarPersona(String id) {
        Persona persona = buscarPersona(id);
        persona.setEliminado(true);
        personaRepository.save(persona);
    }
}
