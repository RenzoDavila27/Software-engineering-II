package com.car.clientead.controller;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.car.clientead.client.dto.enums.RolUsuario;
import com.car.clientead.web.session.UserSession;

/**
 * Controlador utilitario para permitir fijar el rol y el cliente activo en
 * sesión mientras se completa la integración con el módulo de autenticación.
 * <p>
 * Para usarlo: /sesion/rol?rol=CLIENTE&clienteId=123&redirect=/alquileres/historial
 * En producción debería ser reemplazado o protegido por el flujo de login.
 */
@Controller
@RequestMapping("/sesion")
public class SesionUsuarioController {

    private final UserSession userSession;

    public SesionUsuarioController(UserSession userSession) {
        this.userSession = userSession;
    }

    @GetMapping("/rol")
    public String actualizarRol(@RequestParam RolUsuario rol,
                                @RequestParam(required = false) String clienteId,
                                @RequestParam(defaultValue = "/") String redirect) {
        userSession.actualizar(rol, clienteId);
        return "redirect:" + normalizarRedirect(redirect);
    }

    private String normalizarRedirect(String redirect) {
        return StringUtils.hasText(redirect) && redirect.startsWith("/") ? redirect : "/";
    }
}

