package com.car.controller.rest;

import com.car.business.domain.Imagen;
import com.car.business.dto.ImagenDto;
import com.car.business.logic.service.ImagenService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/imagenes")
public class ImagenController extends BaseController<Imagen, ImagenDto, String> {

    public ImagenController(ImagenService service) {
        super(service);
    }
}
