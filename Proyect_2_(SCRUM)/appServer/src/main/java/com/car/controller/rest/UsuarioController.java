package com.car.controller.rest;

import com.car.business.domain.Usuario;
import com.car.business.logic.service.UsuarioService;
import com.car.business.dto.UsuarioApiDto;
import com.car.business.dto.UsuarioDto;
import com.car.business.logic.error.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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

    @PostMapping("/register")
    public ResponseEntity<UsuarioDto> register(@RequestBody UsuarioApiDto usuarioApiDto) {
        try {
            UsuarioDto registeredUser = usuarioService.registerUser(usuarioApiDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (BusinessException e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
