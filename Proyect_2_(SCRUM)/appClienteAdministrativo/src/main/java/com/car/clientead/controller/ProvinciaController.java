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

import com.car.clientead.business.logic.PaisService;
import com.car.clientead.business.logic.ProvinciaService;
import com.car.clientead.client.dto.PaisDto;
import com.car.clientead.client.dto.ProvinciaDto;
import com.car.clientead.client.exception.ApiClientException;

@Controller
@RequestMapping("/provincias")
public class ProvinciaController {

    private static final String REDIRECT_PROVINCIAS = "redirect:/provincias";
    private static final String FORM_VIEW = "eProvincia.html";
    private static final String LIST_VIEW = "lProvincia.html";

    @Autowired
    private ProvinciaService provinciaService;

    @Autowired
    private PaisService paisService;

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("items", provinciaService.listar());
        } catch (ApiClientException ex) {
            model.addAttribute("items", Collections.emptyList());
            model.addAttribute("errorMessage", ex.getMessage());
        }
        model.addAttribute("titleList", "Listado de Provincias");
        return LIST_VIEW;
    }

    @GetMapping("/alta")
    public String mostrarFormularioAlta(Model model) {
        model.addAttribute("item", new ProvinciaDto());
        model.addAttribute("titleForm", "Alta de Provincia");
        model.addAttribute("modoVer", false);
        cargarPaises(model);
        return FORM_VIEW;
    }

    @PostMapping("/alta")
    public String crear(@ModelAttribute ProvinciaDto dto, Model model) {
        try {
            provinciaService.crear(dto);
            return REDIRECT_PROVINCIAS;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("item", dto);
            model.addAttribute("titleForm", "Alta de Provincia");
            model.addAttribute("modoVer", false);
            cargarPaises(model);
            return FORM_VIEW;
        }
    }

    @GetMapping("/consultar/{id}")
    public String consultar(@PathVariable String id, Model model) {
        try {
            model.addAttribute("item", provinciaService.consultar(id));
            model.addAttribute("titleForm", "Detalle de Provincia");
            model.addAttribute("modoVer", true);
            cargarPaises(model);
            return FORM_VIEW;
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_PROVINCIAS;
        }
    }

    @GetMapping("/modificar/{id}")
    public String editar(@PathVariable String id, Model model) {
        try {
            model.addAttribute("item", provinciaService.consultar(id));
            model.addAttribute("titleForm", "Modificar Provincia");
            model.addAttribute("modoVer", false);
            cargarPaises(model);
            return FORM_VIEW;
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_PROVINCIAS;
        }
    }

    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable String id, @ModelAttribute ProvinciaDto dto, Model model) {
        try {
            provinciaService.modificar(id, dto);
            return REDIRECT_PROVINCIAS;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("item", dto);
            model.addAttribute("titleForm", "Modificar Provincia");
            model.addAttribute("modoVer", false);
            cargarPaises(model);
            return FORM_VIEW;
        }
    }

    @GetMapping("/baja/{id}")
    public String eliminar(@PathVariable String id) {
        try {
            provinciaService.eliminar(id);
        } catch (ApiClientException ex) {
            System.err.println("Error al eliminar provincia: " + ex.getMessage());
        }
        return REDIRECT_PROVINCIAS;
    }

    private void cargarPaises(Model model) {
        try {
            model.addAttribute("paises", paisService.listarPaises());
        } catch (ApiClientException ex) {
            model.addAttribute("paises", Collections.<PaisDto>emptyList());
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
