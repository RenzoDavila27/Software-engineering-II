package com.car.controller.rest;

import com.car.business.domain.CostoVehiculo;
import com.car.business.dto.CostoVehiculoDto;
import com.car.business.logic.service.CostoVehiculoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/costos-vehiculo")
public class CostoVehiculoController extends BaseController<CostoVehiculo, CostoVehiculoDto, String> {

    public CostoVehiculoController(CostoVehiculoService service) {
        super(service);
    }
}
