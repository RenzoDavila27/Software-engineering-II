package com.car.controller.rest;

import com.car.business.domain.Usuario;
import com.car.business.dto.UsuarioDto;
import com.car.business.logic.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController extends BaseController<Usuario, UsuarioDto, String> {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService service) {
        super(service);
        this.usuarioService = service;
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody String nombreUsuario) {
        Optional<Usuario> usuario = usuarioService.findByNombreUsuario(nombreUsuario);
        return usuario.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
