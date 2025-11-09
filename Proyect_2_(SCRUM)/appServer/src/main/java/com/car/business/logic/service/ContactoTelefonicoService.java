package com.car.business.logic.service;

import com.car.business.domain.ContactoTelefonico;
import com.car.business.dto.ContactoTelefonicoDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.ContactoTelefonicoMapper;
import com.car.business.percistence.repository.ContactoTelefonicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ContactoTelefonicoService extends BaseService<ContactoTelefonico, ContactoTelefonicoDto, String> {

    public ContactoTelefonicoService(ContactoTelefonicoRepository repository, ContactoTelefonicoMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected void validar(ContactoTelefonico entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("El contacto telefónico es obligatorio.");
        }
        if (entidad.getTipoContacto() == null) {
            throw new BusinessException("El tipo de contacto es obligatorio.");
        }
        if (!StringUtils.hasText(entidad.getTelefono())) {
            throw new BusinessException("El teléfono es obligatorio.");
        }
        if (entidad.getTipoTelefono() == null) {
            throw new BusinessException("El tipo de teléfono es obligatorio.");
        }
    }
}
