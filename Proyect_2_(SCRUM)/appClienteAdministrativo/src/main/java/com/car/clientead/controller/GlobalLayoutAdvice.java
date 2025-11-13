package com.car.clientead.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.car.clientead.client.dto.enums.RolUsuario;
import com.car.clientead.web.session.UserSession;

@ControllerAdvice
public class GlobalLayoutAdvice {

    private final UserSession userSession;

    public GlobalLayoutAdvice(UserSession userSession) {
        this.userSession = userSession;
    }

    @ModelAttribute("rolActual")
    public RolUsuario exponerRolActual() {
        return userSession.getRolActual();
    }

    @ModelAttribute("esRolAdministrativo")
    public boolean esRolAdministrativo() {
        return userSession.getRolActual() == RolUsuario.ADMINISTRATIVO;
    }

    @ModelAttribute("esRolJefe")
    public boolean esRolJefe() {
        return userSession.getRolActual() == RolUsuario.JEFE;
    }

    @ModelAttribute("clienteSesionId")
    public String exponerClienteSesion() {
        return userSession.getClienteId().orElse(null);
    }

    @ModelAttribute("esRolCliente")
    public boolean esRolCliente() {
        return userSession.getRolActual() == RolUsuario.CLIENTE;
    }

    @ModelAttribute("esRolOperativo")
    public boolean esRolOperativo() {
        RolUsuario rol = userSession.getRolActual();
        return rol == RolUsuario.ADMINISTRATIVO || rol == RolUsuario.JEFE;
    }
}
