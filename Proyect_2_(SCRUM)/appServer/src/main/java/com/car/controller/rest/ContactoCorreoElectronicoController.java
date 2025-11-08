package com.car.controller.rest;

import com.car.business.domain.ContactoCorreoElectronico;
import com.car.business.dto.ContactoCorreoElectronicoDto;
import com.car.business.logic.service.ContactoCorreoElectronicoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contactos-correo")
public class ContactoCorreoElectronicoController extends BaseController<ContactoCorreoElectronico, ContactoCorreoElectronicoDto, String> {

    public ContactoCorreoElectronicoController(ContactoCorreoElectronicoService service) {
        super(service);
    }
}
