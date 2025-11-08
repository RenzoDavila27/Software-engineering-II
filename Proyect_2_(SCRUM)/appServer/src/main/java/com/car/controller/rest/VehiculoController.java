package com.car.controller.rest;

import com.car.business.domain.Vehiculo;
import com.car.business.dto.VehiculoDto;
import com.car.business.logic.service.VehiculoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController extends BaseController<Vehiculo, VehiculoDto, String> {

    public VehiculoController(VehiculoService service) {
        super(service);
    }
}
