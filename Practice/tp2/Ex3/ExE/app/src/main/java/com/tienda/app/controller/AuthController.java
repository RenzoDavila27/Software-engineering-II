package com.tienda.app.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tienda.app.business.domain.Usuario;
import com.tienda.app.business.logic.error.ErrorServiceException;
import com.tienda.app.business.logic.service.UsuarioService;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(value = "redirect", required = false) String redirect, Model model,
                               HttpSession session) {
        if (session.getAttribute("usuarioActual") != null) {
            return "redirect:/";
        }
        if (!model.containsAttribute("nombre")) {
            model.addAttribute("nombre", "");
        }
        model.addAttribute("redirect", redirect);
        return "view/login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam("nombre") String nombre,
                                @RequestParam("password") String password,
                                @RequestParam(value = "redirect", required = false) String redirect,
                                HttpSession session,
                                Model model) {
        try {
            Usuario usuario = usuarioService.autenticar(nombre, password);
            session.setAttribute("usuarioActual", usuario);
            if (redirect != null && !redirect.isBlank()) {
                return "redirect:" + redirect;
            }
            return "redirect:/";
        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
            model.addAttribute("nombre", nombre);
            model.addAttribute("redirect", redirect);
            return "view/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("msgExito", "Sesión cerrada correctamente.");
        return "redirect:/login";
    }
}
