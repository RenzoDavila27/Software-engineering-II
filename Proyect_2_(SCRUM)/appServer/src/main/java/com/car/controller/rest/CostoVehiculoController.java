package com.car.controller.rest;

import com.car.business.domain.CostoVehiculo;
import com.car.business.dto.CostoVehiculoDto;
import com.car.business.logic.service.CostoVehiculoService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/costos-vehiculo")
public class CostoVehiculoController extends BaseController<CostoVehiculo, CostoVehiculoDto, String> {

    @Autowired 
    private CostoVehiculoService costoVehiculoService;

    public CostoVehiculoController(CostoVehiculoService service) {
        super(service);
    }

    @GetMapping("/listar/{id}")
    public List<CostoVehiculo> listarCostosPorVehiculo(@PathVariable String id){
        return costoVehiculoService.listarCostosPorVehiculo(id);

    }
}
