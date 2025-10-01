package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.domain.Localidad;
import com.fioritech.demo.bussines.logic.service.DepartamentoService;
import com.fioritech.demo.bussines.logic.service.LocalidadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/localidad")
public class LocalidadController {

    private final LocalidadService localidadService;
    private final DepartamentoService departamentoService;

    public LocalidadController(LocalidadService localidadService,
                               DepartamentoService departamentoService) {
        this.localidadService = localidadService;
        this.departamentoService = departamentoService;
    }

    @GetMapping("/listar")
    public String listarLocalidades(Model model) {
        model.addAttribute("localidades", localidadService.listarLocalidades());
        return "localidad/listar";
    }

    @GetMapping("/crear")
    public String crearLocalidadForm(Model model) {
        model.addAttribute("localidad", new Localidad());
        model.addAttribute("departamentos", departamentoService.listarDepartamentos());
        return "localidad/crear";
    }

    @PostMapping("/crear")
    public String crearLocalidad(@ModelAttribute Localidad localidad) {
        localidadService.crearLocalidad(localidad);
        return "redirect:/localidad/listar";
    }

    @GetMapping("/modificar/{id}")
    public String modificarLocalidadForm(@PathVariable Long id, Model model) {
        model.addAttribute("localidad", localidadService.buscarLocalidadPorId(id));
        model.addAttribute("departamentos", departamentoService.listarDepartamentos());
        return "localidad/modificar";
    }

    @PostMapping("/modificar/{id}")
    public String modificarLocalidad(@PathVariable Long id, @ModelAttribute Localidad cambios) {
        localidadService.modificarLocalidad(id, cambios);
        return "redirect:/localidad/listar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarLocalidad(@PathVariable Long id) {
        localidadService.eliminarLocalidad(id);
        return "redirect:/localidad/listar";
    }
}
