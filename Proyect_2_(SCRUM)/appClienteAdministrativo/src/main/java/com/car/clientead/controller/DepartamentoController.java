package com.car.clientead.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.car.clientead.business.logic.DepartamentoService;
import com.car.clientead.business.logic.ProvinciaService;
import com.car.clientead.client.dto.DepartamentoDto;
import com.car.clientead.client.dto.ProvinciaDto;
import com.car.clientead.client.exception.ApiClientException;

@Controller
@RequestMapping("/departamentos")
public class DepartamentoController {

    private static final String REDIRECT_DEPARTAMENTOS = "redirect:/departamentos";
    private static final String FORM_VIEW = "eDepartamento.html";
    private static final String LIST_VIEW = "lDepartamento.html";

    @Autowired
    private DepartamentoService departamentoService;

    @Autowired
    private ProvinciaService provinciaService;

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("items", departamentoService.listar());
        } catch (ApiClientException ex) {
            model.addAttribute("items", Collections.emptyList());
            model.addAttribute("errorMessage", ex.getMessage());
        }
        model.addAttribute("titleList", "Listado de Departamentos");
        return LIST_VIEW;
    }

    @GetMapping("/alta")
    public String mostrarFormularioAlta(Model model) {
        model.addAttribute("item", new DepartamentoDto());
        model.addAttribute("titleForm", "Alta de Departamento");
        model.addAttribute("modoVer", false);
        cargarProvincias(model);
        return FORM_VIEW;
    }

    @PostMapping("/alta")
    public String crear(@ModelAttribute DepartamentoDto dto, Model model) {
        try {
            departamentoService.crear(dto);
            return REDIRECT_DEPARTAMENTOS;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("item", dto);
            model.addAttribute("titleForm", "Alta de Departamento");
            model.addAttribute("modoVer", false);
            cargarProvincias(model);
            return FORM_VIEW;
        }
    }

    @GetMapping("/consultar/{id}")
    public String consultar(@PathVariable String id, Model model) {
        try {
            model.addAttribute("item", departamentoService.consultar(id));
            model.addAttribute("titleForm", "Detalle de Departamento");
            model.addAttribute("modoVer", true);
            cargarProvincias(model);
            return FORM_VIEW;
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_DEPARTAMENTOS;
        }
    }

    @GetMapping("/modificar/{id}")
    public String editar(@PathVariable String id, Model model) {
        try {
            model.addAttribute("item", departamentoService.consultar(id));
            model.addAttribute("titleForm", "Modificar Departamento");
            model.addAttribute("modoVer", false);
            cargarProvincias(model);
            return FORM_VIEW;
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_DEPARTAMENTOS;
        }
    }

    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable String id, @ModelAttribute DepartamentoDto dto, Model model) {
        try {
            departamentoService.modificar(id, dto);
            return REDIRECT_DEPARTAMENTOS;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("item", dto);
            model.addAttribute("titleForm", "Modificar Departamento");
            model.addAttribute("modoVer", false);
            cargarProvincias(model);
            return FORM_VIEW;
        }
    }

    @GetMapping("/baja/{id}")
    public String eliminar(@PathVariable String id) {
        try {
            departamentoService.eliminar(id);
        } catch (ApiClientException ex) {
            System.err.println("Error al eliminar departamento: " + ex.getMessage());
        }
        return REDIRECT_DEPARTAMENTOS;
    }

    private void cargarProvincias(Model model) {
        try {
            model.addAttribute("provincias", provinciaService.listar());
        } catch (ApiClientException ex) {
            model.addAttribute("provincias", Collections.<ProvinciaDto>emptyList());
            appendError(model, ex.getMessage());
        }
    }

    private void appendError(Model model, String newMessage) {
        Object existing = model.asMap().get("errorMessage");
        String message = existing != null
                ? existing.toString() + " " + newMessage
                : newMessage;
        model.addAttribute("errorMessage", message);
    }
}
