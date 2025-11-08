package com.car.controller.rest;

import com.car.business.domain.Empresa;
import com.car.business.dto.EmpresaDto;
import com.car.business.logic.service.EmpresaService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController extends BaseController<Empresa, EmpresaDto, String> {

    public EmpresaController(EmpresaService service) {
        super(service);
    }
}
