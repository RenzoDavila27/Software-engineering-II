package com.car.controller.rest;

import com.car.business.domain.FormaDePago;
import com.car.business.dto.FormaDePagoDto;
import com.car.business.logic.service.FormaDePagoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/formas-pago")
public class FormaDePagoController extends BaseController<FormaDePago, FormaDePagoDto, String> {

    public FormaDePagoController(FormaDePagoService service) {
        super(service);
    }
}
