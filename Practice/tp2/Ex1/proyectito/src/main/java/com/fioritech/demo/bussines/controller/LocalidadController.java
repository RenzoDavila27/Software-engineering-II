package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.domain.Localidad;
import com.fioritech.demo.bussines.logic.service.DepartamentoService;
import com.fioritech.demo.bussines.logic.service.LocalidadService;
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
@RequestMapping("/localidad")
public class LocalidadController {

    private final LocalidadService localidadService;
    private final DepartamentoService departamentoService;
    private final ProvinciaService provinciaService;
    private final PaisService paisService;

    public LocalidadController(LocalidadService localidadService,
                               DepartamentoService departamentoService,ProvinciaService provinciaService,PaisService paisService) {
        this.localidadService = localidadService;
        this.departamentoService = departamentoService;
        this.provinciaService = provinciaService;
        this.paisService = paisService;
    }

    @GetMapping("/listar")
    public String listarLocalidades(Model model) {
        model.addAttribute("listaLocalidad", localidadService.listarLocalidades());
        return "direccion/localidad/listarLocalidad";
    }

    @GetMapping("/crearForm")
    public String crearLocalidadForm(Model model) {
        model.addAttribute("localidad", new Localidad());
        model.addAttribute("listaDepartamento", departamentoService.listarDepartamentos());
         model.addAttribute("listaProvincia", provinciaService.listarProvincias());
          model.addAttribute("listaPais", paisService.listarPaises());
        return "direccion/localidad/crearLocalidad";
    }

    @PostMapping("/crear")
    public String crearLocalidad(@ModelAttribute Localidad localidad) {
        localidadService.crearLocalidad(localidad);
        return "redirect:/localidad/listar";
    }

    @GetMapping("/modificarForm/{id}")
    public String modificarLocalidadForm(@PathVariable Long id, Model model) {
        model.addAttribute("localidad", localidadService.buscarLocalidadPorId(id));
        model.addAttribute("listaDepartamento", departamentoService.listarDepartamentos());
        model.addAttribute("listaProvincia", provinciaService.listarProvincias());
          model.addAttribute("listaPais", paisService.listarPaises());
        return "direccion/localidad/editarLocalidad";
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

    @GetMapping("/volverEdit")
    public String volver() {
        return "redirect:/localidad/listar";
    }
}
