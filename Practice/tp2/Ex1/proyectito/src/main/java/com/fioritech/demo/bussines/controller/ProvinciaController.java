package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.domain.Provincia;
import com.fioritech.demo.bussines.logic.service.PaisService;
import com.fioritech.demo.bussines.logic.service.ProvinciaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/provincia")
public class ProvinciaController {

    private final ProvinciaService provinciaService;
    private final PaisService paisService;

    public ProvinciaController(ProvinciaService provinciaService, PaisService paisService) {
        this.provinciaService = provinciaService;
        this.paisService = paisService;
    }

    @GetMapping("/listar")
    public String listarProvincias(Model model) {
        model.addAttribute("listaProvincia", provinciaService.listarProvincias());
        return "direccion/provincia/listarProvincia";
    }

    @GetMapping("/crear")
    public String crearProvincia(Model model) {
        model.addAttribute("provincia", new Provincia());
        model.addAttribute("listaPais", paisService.listarPaises());
        return "direccion/provincia/crearProvincia";
    }

    @PostMapping("/crearForm")
    public String crearProvincia(@ModelAttribute Provincia provincia) {
        provinciaService.crearProvincia(provincia);
        return "redirect:/provincia/listar";
    }

    @GetMapping("/modificarForm/{id}")
    public String modificarProvinciaForm(@PathVariable Long id, Model model) {
        model.addAttribute("provincia", provinciaService.buscarProvinciaPorId(id));
        model.addAttribute("listaPais", paisService.listarPaises());
        return "direccion/provincia/editarProvincia";
    }

    @PostMapping("/modificar/{id}")
    public String modificarProvincia(@PathVariable Long id, @ModelAttribute Provincia cambios) {
        provinciaService.modificarProvincia(id, cambios);
        return "redirect:/provincia/listar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProvincia(@PathVariable Long id) {
        provinciaService.eliminarProvincia(id);
        return "redirect:/provincia/listar";
    }

    @GetMapping("/volverEdit")
    public String volver() {
        return "redirect:/provincia/listar";
    }
}
