package com.fioritech.gimnasio.controller.view;

import com.fioritech.gimnasio.business.domain.CuotaMensual;
import com.fioritech.gimnasio.business.domain.Socio;
import com.fioritech.gimnasio.business.domain.ValorCuota;
import com.fioritech.gimnasio.business.domain.enums.EstadoCuotaMensual;
import com.fioritech.gimnasio.business.domain.enums.Mes;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.logic.service.CuotaMensualService;
import com.fioritech.gimnasio.business.logic.service.SocioService;
import com.fioritech.gimnasio.business.logic.service.ValorCuotaService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CuotaMensualController {

    private final CuotaMensualService cuotaMensualService;
    private final SocioService socioService;
    private final ValorCuotaService valorCuotaService;

    public CuotaMensualController(CuotaMensualService cuotaMensualService, SocioService socioService,
        ValorCuotaService valorCuotaService) {
        this.cuotaMensualService = cuotaMensualService;
        this.socioService = socioService;
        this.valorCuotaService = valorCuotaService;
    }

    @GetMapping("/cuotaMensual/listaCuotaMensual")
    public String listaCuotas(Model model) {
        try {
            List<CuotaMensual> listaCuotas = cuotaMensualService.listarCuotaMensualActiva();
            model.addAttribute("listaCuotaMensual", listaCuotas);
            return "view/cuotaMensual/lCuotaMensual";
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            return "view/cuotaMensual/lCuotaMensual";
        }
    }

    @GetMapping("/cuotaMensual/altaCuotaMensual")
    public String alta(CuotaMensual cuotaMensual, Model model) {
        model.addAttribute("isDisabled", false);
        model.addAttribute("Mes", Mes.values());
        model.addAttribute("estadoCuotaMensual", EstadoCuotaMensual.values());
        model.addAttribute("cuotaMensual", cuotaMensual);
        return "view/cuotaMensual/eCuotaMensual";
    }

    @GetMapping("/cuotaMensual/consultar/{id}")
    public String consultar(@PathVariable("id") String idCuotaMensual, Model model, RedirectAttributes attributes) {
        try {
            CuotaMensual cuotaMensual = cuotaMensualService.buscarCuotaMensual(idCuotaMensual);
            model.addAttribute("cuotaMensual", cuotaMensual);
            model.addAttribute("isDisabled", true);
            model.addAttribute("Mes", Mes.values());
            model.addAttribute("estadoCuotaMensual", EstadoCuotaMensual.values());
            return "view/cuotaMensual/eCuotaMensual";
        } catch (BusinessException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
            return "redirect:/cuotaMensual/listaCuotaMensual";
        }
    }

    @GetMapping("/cuotaMensual/modificar/{id}")
    public String modificar(@PathVariable("id") String idCuotaMensual, Model model, RedirectAttributes attributes) {
        try {
            CuotaMensual cuotaMensual = cuotaMensualService.buscarCuotaMensual(idCuotaMensual);
            model.addAttribute("cuotaMensual", cuotaMensual);
            model.addAttribute("Mes", Mes.values());
            model.addAttribute("estadoCuotaMensual", EstadoCuotaMensual.values());
            model.addAttribute("isDisabled", false);
            return "view/cuotaMensual/eCuotaMensual";
        } catch (BusinessException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
            return "redirect:/cuotaMensual/listaCuotaMensual";
        }
    }

    @GetMapping("/cuotaMensual/baja/{id}")
    public String baja(@PathVariable("id") String idCuotaMensual, RedirectAttributes attributes) {
        try {
            cuotaMensualService.eliminarCuotaMensual(idCuotaMensual);
            attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
            return "redirect:/cuotaMensual/listaCuotaMensual";
        } catch (BusinessException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
            return "redirect:/cuotaMensual/listaCuotaMensual";
        }
    }

             
    @PostMapping("/cuotaMensual/aceptarEditCuotaMensual")
    public String aceptarEdit(CuotaMensual cuotaMensual, BindingResult result, RedirectAttributes attributes,
        Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("msgError", "Error de Sistema");
                model.addAttribute("Mes", Mes.values());
                model.addAttribute("estadoCuotaMensual", EstadoCuotaMensual.values());
                return "view/cuotaMensual/eCuotaMensual";
            }
            cuotaMensualService.modificarCuota(cuotaMensual.getId(),cuotaMensual.getMes(),cuotaMensual.getAnio(),cuotaMensual.getEstado());
            attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
            return "redirect:/cuotaMensual/listaCuotaMensual";

        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            model.addAttribute("Mes", Mes.values());
            model.addAttribute("estadoCuotaMensual", EstadoCuotaMensual.values());
            return "view/cuotaMensual/eCuotaMensual";
        } catch (Exception e) {
            model.addAttribute("msgError", "Error de Sistema");
            model.addAttribute("Mes", Mes.values());
            model.addAttribute("estadoCuotaMensual", EstadoCuotaMensual.values());

            return "view/cuotaMensual/eCuotaMensual";
        }
    }

    @GetMapping("/cuotaMensual/cancelarEditCuotaMensual")
    public String cancelarEdit() {
        return "redirect:/cuotaMensual/listaCuotaMensual";
    }
    
}
