package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.domain.Pais;
import com.fioritech.demo.bussines.logic.service.PaisService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pais")
public class PaisController {

    private final PaisService paisService;

    public PaisController(PaisService paisService) {
        this.paisService = paisService;
    }

    @GetMapping("/listar")
    public String listarPaises(Model model) {
        model.addAttribute("paises", paisService.listarPaises());
        return "pais/listar";
    }

    @GetMapping("/crear")
    public String crearPaisForm(Model model) {
        model.addAttribute("pais", new Pais());
        return "pais/crear";
    }

    @PostMapping("/crear")
    public String crearPais(@ModelAttribute Pais pais) {
        paisService.crearPais(pais);
        return "redirect:/pais/listar";
    }

    @GetMapping("/modificar/{id}")
    public String modificarPaisForm(@PathVariable Long id, Model model) {
        model.addAttribute("pais", paisService.buscarPaisPorId(id));
        return "pais/modificar";
    }

    @PostMapping("/modificar/{id}")
    public String modificarPais(@PathVariable Long id, @ModelAttribute Pais cambios) {
        paisService.modificarPais(id, cambios);
        return "redirect:/pais/listar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPais(@PathVariable Long id) {
        paisService.eliminarPais(id);
        return "redirect:/pais/listar";
    }
}
