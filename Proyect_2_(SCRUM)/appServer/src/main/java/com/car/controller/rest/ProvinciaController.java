package com.car.controller.rest;

import com.car.business.domain.Provincia;
import com.car.business.dto.ProvinciaDto;
import com.car.business.logic.service.ProvinciaService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/provincias")
public class ProvinciaController extends BaseController<Provincia, ProvinciaDto, String> {

    public ProvinciaController(ProvinciaService service) {
        super(service);
    }
}
