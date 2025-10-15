package com.tienda.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tienda.app.business.domain.Articulo;
import com.tienda.app.business.logic.error.ErrorServiceException;
import com.tienda.app.business.logic.service.ArticuloService;

import java.util.List;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    private final ArticuloService articuloService;

    public HomeController(ArticuloService articuloService) {
        this.articuloService = articuloService;
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) throws ErrorServiceException {
        List<Articulo> articulos = articuloService.listarActivos();
        model.addAttribute("pageTitle", "Tienda App");
        model.addAttribute("articulos", articulos);
        model.addAttribute("usuarioActual", session.getAttribute("usuarioActual"));
        return "view/index";
    }
}
