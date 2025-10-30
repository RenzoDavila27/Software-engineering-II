package com.example.mecanic.controller;

import com.example.mecanic.bussines.domain.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.mecanic.bussines.logic.service.inicioService;
import com.example.mecanic.bussines.logic.error.ErrorServiceException;

@Controller
public class InicioController {

    @Autowired
    private inicioService inicioService;


    @GetMapping("/")
    public String index(ModelMap modelo) {
        try {
            inicioService.crearUserDefault();
        } catch (ErrorServiceException e) {
            modelo.put("error", "Error al crear usuario por defecto: " + e.getMessage());
        }
        return "index.html";
    }


    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        ModelMap modelo) {
        if (error != null) {
            modelo.put("error", "Usuario o clave incorrecta");
        }
        return "login.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MECANICO')")
    @GetMapping("/inicio")
    public String inicio(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");

        if (usuario != null) {
            if ("ADMIN".equals(usuario.getRol().toString()) || "MECANICO".equals(usuario.getRol().toString()) ) {
                return "inicio.html";
            } else {
                return "login.html"; 
            }
        } else {
            return "login.html";
        }
    }
}
