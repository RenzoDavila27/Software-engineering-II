package com.car.business.logic.service;

import com.car.business.domain.ContactoCorreoElectronico;
import com.car.business.dto.ContactoCorreoElectronicoDto;
import com.car.business.mappers.ContactoCorreoElectronicoMapper;
import com.car.business.percistence.repository.ContactoCorreoElectronicoRepository;
import org.springframework.stereotype.Service;

@Service
public class ContactoCorreoElectronicoService extends BaseService<ContactoCorreoElectronico, ContactoCorreoElectronicoDto, String> {

    public ContactoCorreoElectronicoService(ContactoCorreoElectronicoRepository repository, ContactoCorreoElectronicoMapper mapper) {
        super(repository, mapper);
    }
}
