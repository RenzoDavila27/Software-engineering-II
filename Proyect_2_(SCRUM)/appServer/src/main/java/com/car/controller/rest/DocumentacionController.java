package com.car.controller.rest;

import com.car.business.domain.Documentacion;
import com.car.business.dto.DocumentacionDto;
import com.car.business.logic.service.DocumentacionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/documentaciones")
public class DocumentacionController extends BaseController<Documentacion, DocumentacionDto, String> {

    public DocumentacionController(DocumentacionService service) {
        super(service);
    }
}
