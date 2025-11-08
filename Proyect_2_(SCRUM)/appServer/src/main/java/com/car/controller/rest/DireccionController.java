package com.car.controller.rest;

import com.car.business.domain.Direccion;
import com.car.business.dto.DireccionDto;
import com.car.business.logic.service.DireccionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/direcciones")
public class DireccionController extends BaseController<Direccion, DireccionDto, String> {

    public DireccionController(DireccionService service) {
        super(service);
    }
}
