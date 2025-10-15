package com.tienda.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tienda.app.business.domain.Imagen;
import com.tienda.app.business.logic.service.ImagenService;

@Controller
@RequestMapping("/imagen")
public class ImagenController extends BaseController<Imagen, Long> {

    public ImagenController(ImagenService service) {
        super(service);
        initController(new Imagen(), "LIST IMAGEN", "EDIT IMAGEN");
    }
}
