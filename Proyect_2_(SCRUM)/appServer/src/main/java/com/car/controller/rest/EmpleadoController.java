package com.car.controller.rest;

import com.car.business.domain.Empleado;
import com.car.business.dto.EmpleadoDto;
import com.car.business.logic.service.EmpleadoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController extends BaseController<Empleado, EmpleadoDto, String> {

    public EmpleadoController(EmpleadoService service) {
        super(service);
    }
}
