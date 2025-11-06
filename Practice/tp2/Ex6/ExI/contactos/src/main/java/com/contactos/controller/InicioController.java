package com.contactos.controller;

import com.contactos.business.domain.Usuario;
import com.contactos.business.logic.error.ErrorServiceException;
import com.contactos.business.logic.service.InicioAplicacionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InicioController {

    private final InicioAplicacionService inicioAplicacionService;

    public InicioController(InicioAplicacionService inicioAplicacionService) {
        this.inicioAplicacionService = inicioAplicacionService;
    }

    @GetMapping("/")
    public String index() throws ErrorServiceException {
        inicioAplicacionService.iniciarAplicacion();
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) throws ErrorServiceException {
        inicioAplicacionService.iniciarAplicacion();
        if (error != null) {
            model.addAttribute("error", "Credenciales inválidas. Intente nuevamente.");
        }
        if (logout != null) {
            model.addAttribute("logout", true);
        }
        return "login";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/inicio")
    public String inicio(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuarioActual", usuario);
        return "inicio";
    }

    @GetMapping("/regresoPage")
    public String regreso() {
        return "redirect:/inicio";
    }
}
