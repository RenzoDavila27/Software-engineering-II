package com.car.controller.rest;

import com.car.business.domain.ContactoTelefonico;
import com.car.business.dto.ContactoTelefonicoDto;
import com.car.business.logic.service.ContactoTelefonicoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contactos-telefonicos")
public class ContactoTelefonicoController extends BaseController<ContactoTelefonico, ContactoTelefonicoDto, String> {

    public ContactoTelefonicoController(ContactoTelefonicoService service) {
        super(service);
    }
}
