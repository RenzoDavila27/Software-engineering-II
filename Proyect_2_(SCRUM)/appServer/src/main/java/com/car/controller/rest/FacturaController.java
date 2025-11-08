package com.car.controller.rest;

import com.car.business.domain.Factura;
import com.car.business.dto.FacturaDto;
import com.car.business.logic.service.FacturaService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController extends BaseController<Factura, FacturaDto, String> {

    public FacturaController(FacturaService service) {
        super(service);
    }
}
