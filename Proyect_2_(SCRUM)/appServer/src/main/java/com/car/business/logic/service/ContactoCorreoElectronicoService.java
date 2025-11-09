package com.car.business.logic.service;

import com.car.business.domain.ContactoCorreoElectronico;
import com.car.business.dto.ContactoCorreoElectronicoDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.ContactoCorreoElectronicoMapper;
import com.car.business.percistence.repository.ContactoCorreoElectronicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ContactoCorreoElectronicoService extends BaseService<ContactoCorreoElectronico, ContactoCorreoElectronicoDto, String> {

    public ContactoCorreoElectronicoService(ContactoCorreoElectronicoRepository repository, ContactoCorreoElectronicoMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected void validar(ContactoCorreoElectronico entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("El contacto de correo es obligatorio.");
        }
        if (entidad.getTipoContacto() == null) {
            throw new BusinessException("El tipo de contacto es obligatorio.");
        }
        if (!StringUtils.hasText(entidad.getEmail())) {
            throw new BusinessException("El email es obligatorio.");
        }
    }
}
