package com.car.clientead.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.car.clientead.business.logic.CaracteristicaVehiculoService;
import com.car.clientead.client.dto.CaracteristicaVehiculoDto;
import com.car.clientead.client.exception.ApiClientException;

@Controller
@RequestMapping("/caracteristicas-vehiculo")
public class CaracteristicaVehiculoController {

    private static final String REDIRECT_LISTA = "redirect:/caracteristicas-vehiculo";

    @Autowired
    private CaracteristicaVehiculoService service;

    // 🔹 Listado
    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("items", service.listar());
        } catch (ApiClientException ex) {
            model.addAttribute("items", Collections.emptyList());
            model.addAttribute("errorMessage", ex.getMessage());
        }
        model.addAttribute("titleList", "Listado de Características de Vehículos");
        return "lCaracteristicaVehiculo.html";
    }

    // 🔹 Formulario de alta
    @GetMapping("/alta")
    public String mostrarFormularioAlta(Model model) {
        model.addAttribute("item", new CaracteristicaVehiculoDto());
        model.addAttribute("titleForm", "Alta de Característica de Vehículo");
        model.addAttribute("modoVer", false); // habilita campos
        return "eCaracteristicaVehiculo.html";
    }
    // 🔹 Guardar (crear)
    @PostMapping("/alta")
    public String crear(@ModelAttribute CaracteristicaVehiculoDto dto,
                        @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                        Model model) {
        try {
            service.crear(dto, imagenFile);
            return REDIRECT_LISTA;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("item", dto);
            return "eCaracteristicaVehiculo.html";
        }
    }

    // 🔹 Consultar detalle
    @GetMapping("/consultar/{id}")
    public String consultar(@PathVariable String id, Model model) {
        try {
            model.addAttribute("item", service.consultar(id));
            model.addAttribute("titleForm", "Detalle de Característica de Vehículo");
            model.addAttribute("modoVer", true); // <--- importante
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_LISTA;
        }
        return "eCaracteristicaVehiculo";
    }

    // 🔹 Formulario de modificación
    @GetMapping("/modificar/{id}")
    public String editar(@PathVariable String id, Model model) {
        try {
            model.addAttribute("item", service.consultar(id));
            model.addAttribute("titleForm", "Modificar Característica de Vehículo");
            model.addAttribute("modoVer", false); // <--- habilita los campos
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_LISTA;
        }
        return "eCaracteristicaVehiculo";
    }

    // 🔹 Actualizar (POST)
    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable String id,
                            @ModelAttribute CaracteristicaVehiculoDto dto,
                            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                            Model model) {
        try {
            service.modificar(id, dto, imagenFile);
            return REDIRECT_LISTA;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("item", dto);
            return "eCaracteristicaVehiculo.html";
        }
    }

    // 🔹 Eliminar
    @GetMapping("/baja/{id}")
    public String eliminar(@PathVariable String id) {
        try {
            service.eliminar(id);
        } catch (ApiClientException ex) {
            System.err.println("Error al eliminar vehículo: " + ex.getMessage());
        }
        return REDIRECT_LISTA;
    }
}