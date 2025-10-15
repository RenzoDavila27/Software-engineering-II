package com.tienda.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tienda.app.business.domain.Detalle;
import com.tienda.app.business.logic.service.DetalleService;

@Controller
@RequestMapping("/detalle")
public class DetalleController extends BaseController<Detalle, Long> {

    public DetalleController(DetalleService service) {
        super(service);
        initController(new Detalle(), "LIST DETALLE", "EDIT DETALLE");
    }
}
