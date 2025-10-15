package com.tienda.app.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.tienda.app.business.domain.Usuario;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("usuarioActual")
    public Usuario usuarioActual(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object usuario = session.getAttribute("usuarioActual");
        if (usuario instanceof Usuario) {
            return (Usuario) usuario;
        }
        return null;
    }

    @ModelAttribute("esAdmin")
    public boolean esAdmin(HttpSession session) {
        if (session == null) {
            return false;
        }
        Object usuario = session.getAttribute("usuarioActual");
        if (usuario instanceof Usuario usuarioObj) {
            return Boolean.TRUE.equals(usuarioObj.getAdministrador());
        }
        return false;
    }
}
