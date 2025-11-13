package com.car.controller.rest;

import com.car.business.domain.Alquiler;
import com.car.business.dto.AlquilerDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.logic.service.AlquilerService;
import com.car.controller.rest.api.dto.PaymentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.car.business.mappers.AlquilerMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/alquileres")
public class AlquilerController extends BaseController<Alquiler, AlquilerDto, String> {

    private final AlquilerService alquilerService;

    @Autowired
    private AlquilerMapper alquilerMapper;

    public AlquilerController(AlquilerService service) {
        super(service);
        this.alquilerService = service;
    }

    @PostMapping("/payment")
    public ResponseEntity<AlquilerDto> efectivoPayment(@Valid @RequestBody PaymentRequest request) throws BusinessException {
        Alquiler alquiler = alquilerService.processPayment(request);
        return ResponseEntity.ok(alquilerMapper.toDto(alquiler));
    }


    @PostMapping("/alquilar")
    public ResponseEntity<?> alquilarVehiculo(@RequestBody AlquilerDto alquilerDto) {
        return super.crear(alquilerDto);
    }

    @PostMapping("/entrega/{id}")
    public ResponseEntity<?> marcarEntrega(@PathVariable String id) {
        try {
            alquilerService.marcarEntrega(id);
            return ResponseEntity.noContent().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/entregaerror/{id}")
    public ResponseEntity<?> marcarEntregaError(@PathVariable String id) {
        try {
            alquilerService.marcarEntregaError(id);
            return ResponseEntity.noContent().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
