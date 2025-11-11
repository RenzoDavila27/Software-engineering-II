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
import com.car.clientead.business.logic.LocalidadService;
import com.car.clientead.client.dto.DepartamentoDto;
import com.car.clientead.client.dto.LocalidadDto;
import com.car.clientead.client.exception.ApiClientException;

@Controller
@RequestMapping("/localidades")
public class LocalidadController {

    private static final String REDIRECT_LOCALIDADES = "redirect:/localidades";
    private static final String FORM_VIEW = "eLocalidad.html";
    private static final String LIST_VIEW = "lLocalidad.html";

    @Autowired
    private LocalidadService localidadService;

    @Autowired
    private DepartamentoService departamentoService;

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("items", localidadService.listar());
        } catch (ApiClientException ex) {
            model.addAttribute("items", Collections.emptyList());
            model.addAttribute("errorMessage", ex.getMessage());
        }
        model.addAttribute("titleList", "Listado de Localidades");
        return LIST_VIEW;
    }

    @GetMapping("/alta")
    public String mostrarFormularioAlta(Model model) {
        model.addAttribute("item", new LocalidadDto());
        model.addAttribute("titleForm", "Alta de Localidad");
        model.addAttribute("modoVer", false);
        cargarDepartamentos(model);
        return FORM_VIEW;
    }

    @PostMapping("/alta")
    public String crear(@ModelAttribute LocalidadDto dto, Model model) {
        try {
            localidadService.crear(dto);
            return REDIRECT_LOCALIDADES;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("item", dto);
            model.addAttribute("titleForm", "Alta de Localidad");
            model.addAttribute("modoVer", false);
            cargarDepartamentos(model);
            return FORM_VIEW;
        }
    }

    @GetMapping("/consultar/{id}")
    public String consultar(@PathVariable String id, Model model) {
        try {
            model.addAttribute("item", localidadService.consultar(id));
            model.addAttribute("titleForm", "Detalle de Localidad");
            model.addAttribute("modoVer", true);
            cargarDepartamentos(model);
            return FORM_VIEW;
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_LOCALIDADES;
        }
    }

    @GetMapping("/modificar/{id}")
    public String editar(@PathVariable String id, Model model) {
        try {
            model.addAttribute("item", localidadService.consultar(id));
            model.addAttribute("titleForm", "Modificar Localidad");
            model.addAttribute("modoVer", false);
            cargarDepartamentos(model);
            return FORM_VIEW;
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_LOCALIDADES;
        }
    }

    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable String id, @ModelAttribute LocalidadDto dto, Model model) {
        try {
            localidadService.modificar(id, dto);
            return REDIRECT_LOCALIDADES;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("item", dto);
            model.addAttribute("titleForm", "Modificar Localidad");
            model.addAttribute("modoVer", false);
            cargarDepartamentos(model);
            return FORM_VIEW;
        }
    }

    @GetMapping("/baja/{id}")
    public String eliminar(@PathVariable String id) {
        try {
            localidadService.eliminar(id);
        } catch (ApiClientException ex) {
            System.err.println("Error al eliminar localidad: " + ex.getMessage());
        }
        return REDIRECT_LOCALIDADES;
    }

    private void cargarDepartamentos(Model model) {
        try {
            model.addAttribute("departamentos", departamentoService.listar());
        } catch (ApiClientException ex) {
            model.addAttribute("departamentos", Collections.<DepartamentoDto>emptyList());
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
