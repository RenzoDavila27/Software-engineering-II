package com.tienda.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tienda.app.business.domain.Usuario;
import com.tienda.app.business.logic.service.UsuarioService;

@Controller
@RequestMapping("/usuario")
public class UsuarioController extends BaseController<Usuario, Long> {

    public UsuarioController(UsuarioService service) {
        super(service);
        initController(new Usuario(), "LIST USUARIO", "EDIT USUARIO");
    }
}
