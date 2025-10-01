package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.domain.Direccion;
import com.fioritech.demo.bussines.logic.service.DireccionService;
import com.fioritech.demo.bussines.logic.service.LocalidadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/direccion")
public class DireccionController {

    private final DireccionService direccionService;
    private final LocalidadService localidadService;

    public DireccionController(DireccionService direccionService, LocalidadService localidadService) {
        this.direccionService = direccionService;
        this.localidadService = localidadService;
    }

    @GetMapping("/listar")
    public String listarDirecciones(Model model) {
        model.addAttribute("direcciones", direccionService.listarDirecciones());
        return "direccion/listar";
    }

    @GetMapping("/crear")
    public String crearDireccionForm(Model model) {
        model.addAttribute("direccion", new Direccion());
        model.addAttribute("localidades", localidadService.listarLocalidades());
        return "direccion/crear";
    }

    @PostMapping("/crear")
    public String crearDireccion(@ModelAttribute Direccion direccion) {
        direccionService.crearDireccion(direccion);
        return "redirect:/direccion/listar";
    }

    @GetMapping("/modificar/{id}")
    public String modificarDireccionForm(@PathVariable Long id, Model model) {
        model.addAttribute("direccion", direccionService.buscarDireccionPorId(id));
        model.addAttribute("localidades", localidadService.listarLocalidades());
        return "direccion/modificar";
    }

    @PostMapping("/modificar/{id}")
    public String modificarDireccion(@PathVariable Long id, @ModelAttribute Direccion cambios) {
        direccionService.modificarDireccion(id, cambios);
        return "redirect:/direccion/listar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarDireccion(@PathVariable Long id) {
        direccionService.eliminarDireccion(id);
        return "redirect:/direccion/listar";
    }
}
