package com.fioritech.gimnasio.controller.view;

import com.fioritech.gimnasio.business.domain.ValorCuota;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.logic.service.ValorCuotaService;

import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ValorCuotaController {

    @Autowired
    private ValorCuotaService service;

    @GetMapping("/valorCuota/listaValorCuota")
    public String listaValorCuota(Model model) {
        try {
            Collection<ValorCuota> listaValores = service.listarValorCuotaActivas();
            model.addAttribute("listaValorCuota", listaValores);
            return "view/valorCuota/lValorCuota";
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("msgError", "Error de Sistema");
        }
        return "view/valorCuota/lValorCuota";
    }

    @GetMapping("/valorCuota/altaValorCuota")
    public String alta(ValorCuota valorCuota, Model model) {
        model.addAttribute("isDisabled", false);
        return "view/valorCuota/eValorCuota";
    }

    @GetMapping("/valorCuota/consultar/{id}")
    public String consultar(@PathVariable("id") String idValorCuota, Model model) {
        try {
            ValorCuota valorCuota = service.buscarValorCuota(idValorCuota);
            model.addAttribute("valorCuota", valorCuota);
            model.addAttribute("isDisabled", true);
            return "view/valorCuota/eValorCuota";
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            return "view/valorCuota/lValorCuota";
        }
    }

    @GetMapping("/valorCuota/modificar/{id}")
    public String modificar(@PathVariable("id") String idValorCuota, Model model) {
        try {
            ValorCuota valorCuota = service.buscarValorCuota(idValorCuota);
            model.addAttribute("valorCuota", valorCuota);
            model.addAttribute("isDisabled", false);
            return "view/valorCuota/eValorCuota";
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            return "view/valorCuota/lValorCuota";
        }
    }

    @GetMapping("/valorCuota/baja/{id}")
    public String baja(@PathVariable("id") String idValorCuota, RedirectAttributes attributes) {
        try {
            service.eliminarValorCuota(idValorCuota);
            attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
            return "redirect:/valorCuota/listaValorCuota";
        } catch (BusinessException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
            return "redirect:/valorCuota/listaValorCuota";
        }
    }

    @PostMapping("/valorCuota/aceptarEditValorCuota")
    public String aceptarEdit(ValorCuota valorCuota, BindingResult result, RedirectAttributes attributes, Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("msgError", "Error de Sistema");
                return "view/valorCuota/eValorCuota";
            }

            if (valorCuota.getId() == null || valorCuota.getId().trim().isEmpty()) {
                service.crearValorCuota(valorCuota.getValorCuota(),valorCuota.getFechaDesde());
            } else {
                service.modificarValorCuota(valorCuota.getId(),valorCuota.getValorCuota());
            }

            attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
            return "redirect:/valorCuota/listaValorCuota";

        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            return "view/valorCuota/eValorCuota";
        } catch (Exception e) {
            model.addAttribute("msgError", "Error de Sistema");
            return "view/valorCuota/eValorCuota";
        }
    }

    @GetMapping("/valorCuota/cancelarEditValorCuota")
    public String cancelarEdit() {
        return "redirect:/valorCuota/listaValorCuota";
    }
}

