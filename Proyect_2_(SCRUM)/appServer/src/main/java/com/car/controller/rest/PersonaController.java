package com.car.controller.rest;

import com.car.business.domain.Persona;
import com.car.business.dto.PersonaDto;
import com.car.business.logic.service.PersonaService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/personas")
public class PersonaController extends BaseController<Persona, PersonaDto, String> {

    public PersonaController(PersonaService service) {
        super(service);
    }
}
