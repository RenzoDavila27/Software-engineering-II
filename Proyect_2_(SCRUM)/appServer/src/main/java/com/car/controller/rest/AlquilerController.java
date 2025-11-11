package com.car.controller.rest;

import com.car.business.domain.Alquiler;
import com.car.business.dto.AlquilerDto;
import com.car.business.logic.service.AlquilerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alquileres")
public class AlquilerController extends BaseController<Alquiler, AlquilerDto, String> {

    public AlquilerController(AlquilerService service) {
        super(service);
    }

    @PostMapping("/alquilar")
    public ResponseEntity<?> alquilarVehiculo(@RequestBody AlquilerDto alquilerDto) {
        return super.crear(alquilerDto);
    }
}
