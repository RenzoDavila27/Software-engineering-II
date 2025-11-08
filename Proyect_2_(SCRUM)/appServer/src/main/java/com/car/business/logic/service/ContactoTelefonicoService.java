package com.car.business.logic.service;

import com.car.business.domain.ContactoTelefonico;
import com.car.business.percistence.repository.ContactoTelefonicoRepository;
import org.springframework.stereotype.Service;

@Service
public class ContactoTelefonicoService extends BaseService<ContactoTelefonico, String> {

    public ContactoTelefonicoService(ContactoTelefonicoRepository repository) {
        super(repository);
    }
}
