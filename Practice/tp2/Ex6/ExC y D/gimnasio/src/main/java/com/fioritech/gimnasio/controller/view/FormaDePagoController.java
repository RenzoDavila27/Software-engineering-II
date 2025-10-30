package com.fioritech.gimnasio.controller.view;

import com.fioritech.gimnasio.business.domain.FormaDePago;
import com.fioritech.gimnasio.business.domain.enums.TipoPago;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.logic.service.FormaDePagoService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FormaDePagoController {

    private final FormaDePagoService service;

    public FormaDePagoController(FormaDePagoService service) {
        this.service = service;
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("tiposPago", TipoPago.values());
    }

    @GetMapping("/formaDePago/listaFormaDePago")
    public String listaFormaDePago(Model model) {
        try {
            List<FormaDePago> listaFormas = service.listarFormasDePagoActivas();
            model.addAttribute("listaFormaDePago", listaFormas);
            return "view/formaDePago/lFormaDePago";
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            return "view/formaDePago/lFormaDePago";
        }
    }

    @GetMapping("/formaDePago/altaFormaDePago")
    public String alta(FormaDePago formaDePago, Model model) {
        model.addAttribute("isDisabled", false);
        cargarCatalogos(model);
        return "view/formaDePago/eFormaDePago";
    }

    @GetMapping("/formaDePago/consultar/{id}")
    public String consultar(@PathVariable("id") String id, Model model, RedirectAttributes attributes) {
        try {
            FormaDePago formaDePago = service.buscarFormaDePago(id);
            model.addAttribute("formaDePago", formaDePago);
            model.addAttribute("isDisabled", true);
            cargarCatalogos(model);
            return "view/formaDePago/eFormaDePago";
        } catch (BusinessException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
            return "redirect:/formaDePago/listaFormaDePago";
        }
    }

    @GetMapping("/formaDePago/modificar/{id}")
    public String modificar(@PathVariable("id") String id, Model model, RedirectAttributes attributes) {
        try {
            FormaDePago formaDePago = service.buscarFormaDePago(id);
            model.addAttribute("formaDePago", formaDePago);
            model.addAttribute("isDisabled", false);
            cargarCatalogos(model);
            return "view/formaDePago/eFormaDePago";
        } catch (BusinessException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
            return "redirect:/formaDePago/listaFormaDePago";
        }
    }

    @GetMapping("/formaDePago/baja/{id}")
    public String baja(@PathVariable("id") String id, RedirectAttributes attributes) {
        try {
            service.eliminarFormaDePago(id);
            attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
            return "redirect:/formaDePago/listaFormaDePago";
        } catch (BusinessException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
            return "redirect:/formaDePago/listaFormaDePago";
        }
    }

    @PostMapping("/formaDePago/aceptarEditFormaDePago")
    public String aceptarEdit(FormaDePago formaDePago, BindingResult result, RedirectAttributes attributes, Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("msgError", "Error de Sistema");
                cargarCatalogos(model);
                return "view/formaDePago/eFormaDePago";
            }

            if (formaDePago.getId() == null || formaDePago.getId().trim().isEmpty()) {
                service.crearFormaDePago(formaDePago.getTipoPago(), formaDePago.getObservacion());
            } else {
                service.modificarFormaDePago(formaDePago.getId(), formaDePago.getTipoPago(), formaDePago.getObservacion());
            }

            attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
            return "redirect:/formaDePago/listaFormaDePago";

        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            cargarCatalogos(model);
            return "view/formaDePago/eFormaDePago";
        } catch (Exception e) {
            model.addAttribute("msgError", "Error de Sistema");
            cargarCatalogos(model);
            return "view/formaDePago/eFormaDePago";
        }
    }

    @GetMapping("/formaDePago/cancelarEditFormaDePago")
    public String cancelarEdit() {
        return "redirect:/formaDePago/listaFormaDePago";
    }
}
