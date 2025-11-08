package com.car.controller.rest;

import com.car.business.domain.DetalleFactura;
import com.car.business.dto.DetalleFacturaDto;
import com.car.business.logic.service.DetalleFacturaService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/detalles-factura")
public class DetalleFacturaController extends BaseController<DetalleFactura, DetalleFacturaDto, String> {

    public DetalleFacturaController(DetalleFacturaService service) {
        super(service);
    }
}
