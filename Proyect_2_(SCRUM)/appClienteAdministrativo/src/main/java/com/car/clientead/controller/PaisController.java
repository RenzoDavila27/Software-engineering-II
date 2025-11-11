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
import com.car.clientead.client.dto.PaisDto;
import com.car.clientead.client.exception.ApiClientException;

@Controller
@RequestMapping("/paises")
public class PaisController {

    private static final String REDIRECT_PAISES = "redirect:/paises";
    private static final String FORM_VIEW = "ePais.html";

    @Autowired
    private PaisService paisService;

    @GetMapping
    public String paises(Model model) {
        try {
            model.addAttribute("items", paisService.listarPaises());
        } catch (ApiClientException ex) {
            model.addAttribute("items", Collections.emptyList());
            Object existing = model.asMap().get("errorMessage");
            String message = existing != null
                    ? existing.toString() + " " + ex.getMessage()
                    : ex.getMessage();
            model.addAttribute("errorMessage", message);
        }
        model.addAttribute("titleList", "Listado de Países");
        return "lPais.html";
    }

    @GetMapping("/alta")
    public String mostrarFormularioAlta(Model model) {
        model.addAttribute("item", new PaisDto());
        model.addAttribute("titleForm", "Alta de País");
        model.addAttribute("modoVer", false);
        return FORM_VIEW;
    }

    @PostMapping("/alta")
    public String crear(@ModelAttribute PaisDto dto, Model model) {
        try {
            paisService.crear(dto);
            return REDIRECT_PAISES;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("item", dto);
            model.addAttribute("titleForm", "Alta de País");
            model.addAttribute("modoVer", false);
            return FORM_VIEW;
        }
    }

    @GetMapping("/consultar/{id}")
    public String consultar(@PathVariable String id, Model model) {
        try {
            model.addAttribute("item", paisService.consultar(id));
            model.addAttribute("titleForm", "Detalle del País");
            model.addAttribute("modoVer", true);
            return FORM_VIEW;
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_PAISES;
        }
    }

    @GetMapping("/modificar/{id}")
    public String editar(@PathVariable String id, Model model) {
        try {
            model.addAttribute("item", paisService.consultar(id));
            model.addAttribute("titleForm", "Modificar País");
            model.addAttribute("modoVer", false);
            return FORM_VIEW;
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_PAISES;
        }
    }

    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable String id, @ModelAttribute PaisDto dto, Model model) {
        try {
            paisService.modificar(id, dto);
            return REDIRECT_PAISES;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("item", dto);
            model.addAttribute("titleForm", "Modificar País");
            model.addAttribute("modoVer", false);
            return FORM_VIEW;
        }
    }

    @GetMapping("/baja/{id}")
    public String eliminar(@PathVariable String id) {
        try {
            paisService.eliminar(id);
        } catch (ApiClientException ex) {
            System.err.println("Error al eliminar país: " + ex.getMessage());
        }
        return REDIRECT_PAISES;
    }
}
