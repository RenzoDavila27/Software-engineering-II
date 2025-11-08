package com.car.controller.rest;

import com.car.business.domain.Departamento;
import com.car.business.dto.DepartamentoDto;
import com.car.business.logic.service.DepartamentoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController extends BaseController<Departamento, DepartamentoDto, String> {

    public DepartamentoController(DepartamentoService service) {
        super(service);
    }
}
