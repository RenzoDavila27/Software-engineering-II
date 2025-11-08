package com.car.controller.rest;

import com.car.business.domain.Cliente;
import com.car.business.dto.ClienteDto;
import com.car.business.logic.service.ClienteService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController extends BaseController<Cliente, ClienteDto, String> {

    public ClienteController(ClienteService service) {
        super(service);
    }
}
