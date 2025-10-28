package com.contactos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.contactos.business.domain.Usuario;
import com.contactos.business.logic.error.ErrorServiceException;
import com.contactos.business.logic.service.InicioAplicacionService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/")
public class InicioController {

    private final InicioAplicacionService inicioAplicacionService;

    public InicioController(InicioAplicacionService inicioAplicacionService) {
        this.inicioAplicacionService = inicioAplicacionService;
    }

    @GetMapping
    public ResponseEntity<String> index() throws ErrorServiceException {
        inicioAplicacionService.iniciarAplicacion();
        return ResponseEntity.ok("Bienvenido al sistema de contactos");
    }

    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestParam(required = false) String error) {
        if (error != null) {
            return ResponseEntity.badRequest().body("Credenciales inválidas");
        }
        return ResponseEntity.ok("Ingrese sus credenciales para acceder");
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/inicio")
    public ResponseEntity<String> inicio(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        if (usuario == null) {
            return ResponseEntity.ok("Sesión no disponible");
        }
        if (usuario.getRol() != null && usuario.getRol().name().equalsIgnoreCase("ADMIN")) {
            return ResponseEntity.ok("Bienvenido al panel administrativo");
        }
        return ResponseEntity.ok("Bienvenido al portal de usuarios");
    }

    @GetMapping("/regresoPage")
    public ResponseEntity<String> regreso() {
        return ResponseEntity.ok("Redirija a /inicio para continuar");
    }
}
