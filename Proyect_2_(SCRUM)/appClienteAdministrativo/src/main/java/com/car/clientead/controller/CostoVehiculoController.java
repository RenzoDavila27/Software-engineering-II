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

import com.car.clientead.business.logic.CostoVehiculoService;
import com.car.clientead.client.dto.CostoVehiculoDto;
import com.car.clientead.client.exception.ApiClientException;

@Controller
@RequestMapping("/costos-vehiculo")
public class CostoVehiculoController {

    private static final String REDIRECT_LISTA = "redirect:/costos-vehiculo";

    @Autowired
    private CostoVehiculoService service;

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("items", service.listar());
        } catch (ApiClientException ex) {
            model.addAttribute("items", Collections.emptyList());
            model.addAttribute("errorMessage", ex.getMessage());
        }
        model.addAttribute("titleList", "Listado de Costos de Vehículo");
        return "lCostoVehiculo.html";
    }

    @GetMapping("/alta")
    public String mostrarFormularioAlta(Model model) {
        prepararFormulario(model, new CostoVehiculoDto(), "Alta de Costo de Vehículo", false);
        return "eCostoVehiculo.html";
    }

    @PostMapping("/alta")
    public String crear(@ModelAttribute CostoVehiculoDto dto, Model model) {
        try {
            service.crear(dto);
            return REDIRECT_LISTA;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            prepararFormulario(model, dto, "Alta de Costo de Vehículo", false);
            return "eCostoVehiculo.html";
        }
    }

    @GetMapping("/consultar/{id}")
    public String consultar(@PathVariable String id, Model model) {
        try {
            CostoVehiculoDto dto = service.consultar(id);
            prepararFormulario(model, dto, "Detalle de Costo de Vehículo", true);
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_LISTA;
        }
        return "eCostoVehiculo.html";
    }

    @GetMapping("/modificar/{id}")
    public String editar(@PathVariable String id, Model model) {
        try {
            CostoVehiculoDto dto = service.consultar(id);
            prepararFormulario(model, dto, "Modificar Costo de Vehículo", false);
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_LISTA;
        }
        return "eCostoVehiculo.html";
    }

    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable String id,
                            @ModelAttribute CostoVehiculoDto dto,
                            Model model) {
        try {
            service.modificar(id, dto);
            return REDIRECT_LISTA;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            prepararFormulario(model, dto, "Modificar Costo de Vehículo", false);
            return "eCostoVehiculo.html";
        }
    }

    @GetMapping("/baja/{id}")
    public String eliminar(@PathVariable String id) {
        try {
            service.eliminar(id);
        } catch (ApiClientException ex) {
            System.err.println("Error al eliminar costo de vehículo: " + ex.getMessage());
        }
        return REDIRECT_LISTA;
    }

    private void prepararFormulario(Model model, CostoVehiculoDto dto, String title, boolean modoVer) {
        model.addAttribute("item", dto);
        model.addAttribute("titleForm", title);
        model.addAttribute("modoVer", modoVer);
    }
}
