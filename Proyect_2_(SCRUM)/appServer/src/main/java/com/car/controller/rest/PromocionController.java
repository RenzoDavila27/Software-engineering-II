package com.car.controller.rest;

import com.car.business.domain.Promocion;
import com.car.business.dto.PromocionDto;
import com.car.business.logic.service.PromocionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/promociones")
public class PromocionController extends BaseController<Promocion, PromocionDto, String> {

    public PromocionController(PromocionService service) {
        super(service);
    }
}
