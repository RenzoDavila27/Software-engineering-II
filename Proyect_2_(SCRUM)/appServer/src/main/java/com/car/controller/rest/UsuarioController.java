package com.car.controller.rest;

import com.car.business.domain.Usuario;
import com.car.business.dto.UsuarioDto;
import com.car.business.logic.service.UsuarioService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController extends BaseController<Usuario, UsuarioDto, String> {

    public UsuarioController(UsuarioService service) {
        super(service);
    }
}
