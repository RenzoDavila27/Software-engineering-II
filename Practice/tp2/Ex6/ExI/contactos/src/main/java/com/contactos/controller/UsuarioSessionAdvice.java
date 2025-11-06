package com.contactos.controller;

import com.contactos.business.domain.Usuario;
import com.contactos.business.domain.enumeration.Rol;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UsuarioSessionAdvice {

    @ModelAttribute("usuarioActual")
    public Usuario usuarioActual(HttpSession session) {
        Object atributo = session != null ? session.getAttribute("usuariosession") : null;
        if (atributo instanceof Usuario usuario) {
            return usuario;
        }
        return null;
    }

    @ModelAttribute("esAdmin")
    public boolean esAdmin(HttpSession session) {
        Object atributo = session != null ? session.getAttribute("usuariosession") : null;
        if (atributo instanceof Usuario usuario) {
            return usuario.getRol() == Rol.ADMIN;
        }
        return false;
    }
}
