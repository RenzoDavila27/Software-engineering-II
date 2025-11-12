package com.car.controller.rest;

import com.car.business.domain.Vehiculo;
import com.car.business.dto.VehiculoDto;
import com.car.business.logic.service.VehiculoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController extends BaseController<Vehiculo, VehiculoDto, String> {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService service) {
        super(service);
        this.vehiculoService = service;
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<VehiculoDto>> buscarVehiculosDisponibles() {
        return ResponseEntity.ok(vehiculoService.findAvailable());
    }
}
