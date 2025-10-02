package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.domain.Promocion;
import com.fioritech.demo.bussines.domain.PromocionTipo;
import com.fioritech.demo.bussines.logic.service.PromocionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

@Controller
@RequestMapping("/promocion")
public class PromocionController {

    private final PromocionService promocionService;

    public PromocionController(PromocionService promocionService) {
        this.promocionService = promocionService;
    }

    @GetMapping("/listar")
    public String listarPromociones(Model model) {
        Collection<Promocion> promocionesGenerales = promocionService.listarPromocionesPorTipo(PromocionTipo.PROMOCION_GENERAL);
        Collection<Promocion> saludosFinAnio = promocionService.listarPromocionesPorTipo(PromocionTipo.SALUDO_FIN_ANIO);
        Promocion promocionActual = promocionesGenerales.stream().findFirst().orElse(null);
        Promocion saludoActual = saludosFinAnio.stream().findFirst().orElse(null);
        model.addAttribute("promocionGeneral", promocionActual);
        model.addAttribute("saludoFinAnio", saludoActual);
        return "promocion/listar";
    }

    @GetMapping("/crear")
    public String crearPromocionForm(@RequestParam(name = "tipo", required = false) PromocionTipo tipo,
                                     Model model) {
        Promocion nuevaPromocion = new Promocion();
        nuevaPromocion.setTipo(tipo != null ? tipo : PromocionTipo.PROMOCION_GENERAL);
        model.addAttribute("promocion", nuevaPromocion);
        model.addAttribute("tipo", nuevaPromocion.getTipo());
        return "promocion/crear";
    }

    @PostMapping("/crear")
    public String crearPromocion(@ModelAttribute Promocion promocion) {
        promocionService.crearPromocion(promocion);
        return "redirect:/promocion/listar";
    }

    @GetMapping("/modificar/{id}")
    public String modificarPromocionForm(@PathVariable Long id, Model model) {
        Promocion promocion = promocionService.buscarPromocionPorId(id);
        model.addAttribute("promocion", promocion);
        model.addAttribute("tipo", promocion.getTipo());
        return "promocion/modificar";
    }

    @PostMapping("/modificar/{id}")
    public String modificarPromocion(@PathVariable Long id, @ModelAttribute Promocion cambios) {
        promocionService.modificarPromocion(id, cambios);
        return "redirect:/promocion/listar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPromocion(@PathVariable Long id) {
        promocionService.eliminarPromocion(id);
        return "redirect:/promocion/listar";
    }
}
