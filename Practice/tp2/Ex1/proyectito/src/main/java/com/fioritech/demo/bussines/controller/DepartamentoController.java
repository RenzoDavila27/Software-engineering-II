package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.domain.Departamento;
import com.fioritech.demo.bussines.logic.service.DepartamentoService;
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
@RequestMapping("/departamento")
public class DepartamentoController {

    private final DepartamentoService departamentoService;
    private final ProvinciaService provinciaService;
    private final PaisService paisService;

    public DepartamentoController(DepartamentoService departamentoService,
                                  ProvinciaService provinciaService, PaisService paisService) {
        this.departamentoService = departamentoService;
        this.provinciaService = provinciaService;
        this.paisService = paisService;
    }

    @GetMapping("/listar")
    public String listarDepartamentos(Model model) {
        model.addAttribute("listaDepartamento", departamentoService.listarDepartamentos());
        return "direccion/departamento/listarDepartamento";
    }

    @GetMapping("/crearForm")
    public String crearDepartamentoForm(Model model) {
        model.addAttribute("departamento", new Departamento());
        model.addAttribute("listaProvincia", provinciaService.listarProvincias());
        model.addAttribute("listaPais", paisService.listarPaises());
        return "direccion/departamento/crearDepartamento";
    }

    @PostMapping("/crear")
    public String crearDepartamento(@ModelAttribute Departamento departamento) {
        departamentoService.crearDepartamento(departamento);
        return "redirect:/departamento/listar";
    }

    @GetMapping("/modificarForm/{id}")
    public String modificarDepartamentoForm(@PathVariable Long id, Model model) {
        model.addAttribute("departamento", departamentoService.buscarDepartamentoPorId(id));
         model.addAttribute("listaProvincia", provinciaService.listarProvincias());
        model.addAttribute("listaPais", paisService.listarPaises());
        return "direccion/departamento/editarDepartamento";
    }

    @PostMapping("/modificar/{id}")
    public String modificarDepartamento(@PathVariable Long id, @ModelAttribute Departamento cambios) {
        departamentoService.modificarDepartamento(id, cambios);
        return "redirect:/departamento/listar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarDepartamento(@PathVariable Long id) {
        departamentoService.eliminarDepartamento(id);
        return "redirect:/departamento/listar";
    }

    @GetMapping("/volverEdit")
    public String volver() {
        return "redirect:/departamento/listar";
    }
}
