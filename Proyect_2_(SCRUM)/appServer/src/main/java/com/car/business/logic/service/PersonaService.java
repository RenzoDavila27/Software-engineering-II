package com.car.business.logic.service;

import com.car.business.domain.Persona;
import com.car.business.percistence.repository.PersonaRepository;
import org.springframework.stereotype.Service;

@Service
public class PersonaService extends BaseService<Persona, String> {

    public PersonaService(PersonaRepository repository) {
        super(repository);
    }
}
