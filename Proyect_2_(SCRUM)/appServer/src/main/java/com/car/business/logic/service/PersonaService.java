package com.car.business.logic.service;

import com.car.business.domain.Persona;
import com.car.business.dto.PersonaDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.PersonaMapper;
import com.car.business.percistence.repository.PersonaRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PersonaService extends BaseService<Persona, PersonaDto, String> {

    public PersonaService(PersonaRepository repository, PersonaMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected void validar(Persona entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("La persona es obligatoria.");
        }
        if (!StringUtils.hasText(entidad.getNombre())) {
            throw new BusinessException("El nombre es obligatorio.");
        }
        if (!StringUtils.hasText(entidad.getApellido())) {
            throw new BusinessException("El apellido es obligatorio.");
        }
        if (entidad.getFechaNacimiento() == null) {
            throw new BusinessException("La fecha de nacimiento es obligatoria.");
        }
        if (entidad.getTipoDocumento() == null) {
            throw new BusinessException("El tipo de documento es obligatorio.");
        }
        if (!StringUtils.hasText(entidad.getNumeroDocumento())) {
            throw new BusinessException("El número de documento es obligatorio.");
        }
        if (entidad.getContactos() == null || entidad.getContactos().isEmpty()) {
            throw new BusinessException("El contacto es obligatorio.");
        }
        if (entidad.getDireccion() == null) {
            throw new BusinessException("La dirección es obligatoria.");
        }
        if (entidad.getImagen() == null) {
            throw new BusinessException("La imagen es obligatoria.");
        }
    }
}
