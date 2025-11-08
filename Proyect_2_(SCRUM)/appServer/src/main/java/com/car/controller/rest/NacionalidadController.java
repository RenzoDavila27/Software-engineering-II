package com.car.controller.rest;

import com.car.business.domain.Nacionalidad;
import com.car.business.dto.NacionalidadDto;
import com.car.business.logic.service.NacionalidadService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/nacionalidades")
public class NacionalidadController extends BaseController<Nacionalidad, NacionalidadDto, String> {

    public NacionalidadController(NacionalidadService service) {
        super(service);
    }
}
