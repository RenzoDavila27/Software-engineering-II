package com.car.business.mappers;

import com.car.business.domain.Persona;
import com.car.business.dto.PersonaDto;
import org.springframework.stereotype.Component;

@Component
public class PersonaMapper extends AbstractPersonaMapper<Persona, PersonaDto> {

    public PersonaMapper(EntityReferenceResolver resolver) {
        super(resolver);
    }

    @Override
    protected PersonaDto createDto() {
        return new PersonaDto();
    }

    @Override
    protected Persona createEntity() {
        return new Persona();
    }
}
