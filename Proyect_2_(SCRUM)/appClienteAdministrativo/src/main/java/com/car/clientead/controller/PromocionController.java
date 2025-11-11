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

import com.car.clientead.business.logic.PromocionService;
import com.car.clientead.client.dto.PromocionDto;
import com.car.clientead.client.exception.ApiClientException;

@Controller
@RequestMapping("/promociones")
public class PromocionController {

    private static final String LIST_VIEW = "lPromocion.html";
    private static final String FORM_VIEW = "ePromocion.html";
    private static final String REDIRECT_LISTA = "redirect:/promociones";

    @Autowired
    private PromocionService service;

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("items", service.listar());
        } catch (ApiClientException ex) {
            model.addAttribute("items", Collections.emptyList());
            model.addAttribute("errorMessage", ex.getMessage());
        }
        model.addAttribute("titleList", "Promociones y descuentos");
        return LIST_VIEW;
    }

    @GetMapping("/alta")
    public String alta(Model model) {
        prepararFormulario(model, new PromocionDto(), "Nueva promoción", false);
        return FORM_VIEW;
    }

    @PostMapping("/alta")
    public String crear(@ModelAttribute PromocionDto dto, Model model) {
        try {
            service.crear(dto);
            return REDIRECT_LISTA;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            prepararFormulario(model, dto, "Nueva promoción", false);
            return FORM_VIEW;
        }
    }

    @GetMapping("/consultar/{id}")
    public String consultar(@PathVariable String id, Model model) {
        try {
            prepararFormulario(model, service.consultar(id), "Detalle de la promoción", true);
            return FORM_VIEW;
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_LISTA;
        }
    }

    @GetMapping("/modificar/{id}")
    public String editar(@PathVariable String id, Model model) {
        try {
            prepararFormulario(model, service.consultar(id), "Modificar promoción", false);
            return FORM_VIEW;
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_LISTA;
        }
    }

    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable String id,
                            @ModelAttribute PromocionDto dto,
                            Model model) {
        try {
            service.modificar(id, dto);
            return REDIRECT_LISTA;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            prepararFormulario(model, dto, "Modificar promoción", false);
            return FORM_VIEW;
        }
    }

    @GetMapping("/baja/{id}")
    public String eliminar(@PathVariable String id) {
        try {
            service.eliminar(id);
        } catch (ApiClientException ex) {
            System.err.println("Error al eliminar promoción: " + ex.getMessage());
        }
        return REDIRECT_LISTA;
    }

    private void prepararFormulario(Model model, PromocionDto dto, String titulo, boolean modoVer) {
        model.addAttribute("item", dto);
        model.addAttribute("titleForm", titulo);
        model.addAttribute("modoVer", modoVer);
    }
}
