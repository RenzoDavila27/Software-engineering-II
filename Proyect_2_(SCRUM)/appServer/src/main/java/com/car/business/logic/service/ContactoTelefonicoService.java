package com.car.business.logic.service;

import com.car.business.domain.ContactoTelefonico;
import com.car.business.dto.ContactoTelefonicoDto;
import com.car.business.mappers.ContactoTelefonicoMapper;
import com.car.business.percistence.repository.ContactoTelefonicoRepository;
import org.springframework.stereotype.Service;

@Service
public class ContactoTelefonicoService extends BaseService<ContactoTelefonico, ContactoTelefonicoDto, String> {

    public ContactoTelefonicoService(ContactoTelefonicoRepository repository, ContactoTelefonicoMapper mapper) {
        super(repository, mapper);
    }
}
