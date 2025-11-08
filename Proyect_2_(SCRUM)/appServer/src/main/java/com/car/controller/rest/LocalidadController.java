package com.car.controller.rest;

import com.car.business.domain.Localidad;
import com.car.business.dto.LocalidadDto;
import com.car.business.logic.service.LocalidadService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/localidades")
public class LocalidadController extends BaseController<Localidad, LocalidadDto, String> {

    public LocalidadController(LocalidadService service) {
        super(service);
    }
}
