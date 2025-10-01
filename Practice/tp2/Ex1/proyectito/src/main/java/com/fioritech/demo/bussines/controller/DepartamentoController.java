package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.domain.Departamento;
import com.fioritech.demo.bussines.logic.service.DepartamentoService;
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

    public DepartamentoController(DepartamentoService departamentoService,
                                  ProvinciaService provinciaService) {
        this.departamentoService = departamentoService;
        this.provinciaService = provinciaService;
    }

    @GetMapping("/listar")
    public String listarDepartamentos(Model model) {
        model.addAttribute("departamentos", departamentoService.listarDepartamentos());
        return "departamento/listar";
    }

    @GetMapping("/crear")
    public String crearDepartamentoForm(Model model) {
        model.addAttribute("departamento", new Departamento());
        model.addAttribute("provincias", provinciaService.listarProvincias());
        return "departamento/crear";
    }

    @PostMapping("/crear")
    public String crearDepartamento(@ModelAttribute Departamento departamento) {
        departamentoService.crearDepartamento(departamento);
        return "redirect:/departamento/listar";
    }

    @GetMapping("/modificar/{id}")
    public String modificarDepartamentoForm(@PathVariable Long id, Model model) {
        model.addAttribute("departamento", departamentoService.buscarDepartamentoPorId(id));
        model.addAttribute("provincias", provinciaService.listarProvincias());
        return "departamento/modificar";
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
}
