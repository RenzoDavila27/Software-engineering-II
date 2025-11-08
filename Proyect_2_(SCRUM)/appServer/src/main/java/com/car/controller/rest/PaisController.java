package com.car.controller.rest;

import com.car.business.domain.Pais;
import com.car.business.dto.PaisDto;
import com.car.business.logic.service.PaisService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paises")
public class PaisController extends BaseController<Pais, PaisDto, String> {

    public PaisController(PaisService service) {
        super(service);
    }
}
