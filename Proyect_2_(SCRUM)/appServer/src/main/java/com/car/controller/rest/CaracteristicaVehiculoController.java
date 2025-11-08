package com.car.controller.rest;

import com.car.business.domain.CaracteristicaVehiculo;
import com.car.business.dto.CaracteristicaVehiculoDto;
import com.car.business.logic.service.CaracteristicaVehiculoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/caracteristicas-vehiculo")
public class CaracteristicaVehiculoController extends BaseController<CaracteristicaVehiculo, CaracteristicaVehiculoDto, String> {

    public CaracteristicaVehiculoController(CaracteristicaVehiculoService service) {
        super(service);
    }
}
