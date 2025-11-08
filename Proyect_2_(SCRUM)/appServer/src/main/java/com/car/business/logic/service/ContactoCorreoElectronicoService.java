package com.car.business.logic.service;

import com.car.business.domain.ContactoCorreoElectronico;
import com.car.business.percistence.repository.ContactoCorreoElectronicoRepository;
import org.springframework.stereotype.Service;

@Service
public class ContactoCorreoElectronicoService extends BaseService<ContactoCorreoElectronico, String> {

    public ContactoCorreoElectronicoService(ContactoCorreoElectronicoRepository repository) {
        super(repository);
    }
}
