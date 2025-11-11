package com.car.clientead.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.util.StringUtils;

import com.car.clientead.business.logic.CaracteristicaVehiculoService;
import com.car.clientead.business.logic.VehiculoService;
import com.car.clientead.client.dto.CaracteristicaVehiculoDto;
import com.car.clientead.client.dto.VehiculoDto;
import com.car.clientead.client.dto.enums.EstadoVehiculo;
import com.car.clientead.client.exception.ApiClientException;

@Controller
@RequestMapping("/vehiculos")
public class VehiculoController {

    private static final String REDIRECT_LISTA = "redirect:/vehiculos";

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private CaracteristicaVehiculoService caracteristicaService;

    @GetMapping
    public String listar(@RequestParam(value = "caracteristicaId", required = false) String caracteristicaId,
                         Model model) {
        try {
            List<VehiculoDto> vehiculos = vehiculoService.listar();
            CaracteristicaVehiculoDto caracteristicaSeleccionada = null;
            if (StringUtils.hasText(caracteristicaId)) {
                caracteristicaSeleccionada = caracteristicaService.consultar(caracteristicaId);
                vehiculos = vehiculos.stream()
                        .filter(v -> caracteristicaId.equals(v.getCaracteristicaVehiculoId()))
                        .collect(Collectors.toList());
            }
            model.addAttribute("items", vehiculos);
            model.addAttribute("caracteristicasMap", obtenerMapaCaracteristicas());
            model.addAttribute("caracteristicaSeleccionada", caracteristicaSeleccionada);
            model.addAttribute("caracteristicaIdFiltro", caracteristicaId);
        } catch (ApiClientException ex) {
            model.addAttribute("items", Collections.emptyList());
            model.addAttribute("caracteristicasMap", Collections.emptyMap());
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("caracteristicaSeleccionada", null);
            model.addAttribute("caracteristicaIdFiltro", caracteristicaId);
        }
        model.addAttribute("titleList", "Listado de Vehículos");
        return "lVehiculo.html";
    }

    @GetMapping("/alta")
    public String mostrarFormularioAlta(@RequestParam(value = "caracteristicaId", required = false) String caracteristicaId,
                                        Model model) {
        VehiculoDto dto = new VehiculoDto();
        if (StringUtils.hasText(caracteristicaId)) {
            dto.setCaracteristicaVehiculoId(caracteristicaId);
        }
        prepararFormulario(model, dto, "Alta de Vehículo", false);
        return "eVehiculo.html";
    }

    @PostMapping("/alta")
    public String crear(@ModelAttribute VehiculoDto dto, Model model) {
        try {
            vehiculoService.crear(dto);
            return construirRedirect(dto.getCaracteristicaVehiculoId());
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            prepararFormulario(model, dto, "Alta de Vehículo", false);
            return "eVehiculo.html";
        }
    }

    @GetMapping("/consultar/{id}")
    public String consultar(@PathVariable String id, Model model) {
        try {
            VehiculoDto dto = vehiculoService.consultar(id);
            prepararFormulario(model, dto, "Detalle de Vehículo", true);
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_LISTA;
        }
        return "eVehiculo.html";
    }

    @GetMapping("/modificar/{id}")
    public String editar(@PathVariable String id, Model model) {
        try {
            VehiculoDto dto = vehiculoService.consultar(id);
            prepararFormulario(model, dto, "Modificar Vehículo", false);
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_LISTA;
        }
        return "eVehiculo.html";
    }

    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable String id,
                            @ModelAttribute VehiculoDto dto,
                            Model model) {
        try {
            vehiculoService.modificar(id, dto);
            return construirRedirect(dto.getCaracteristicaVehiculoId());
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            prepararFormulario(model, dto, "Modificar Vehículo", false);
            return "eVehiculo.html";
        }
    }

    @GetMapping("/baja/{id}")
    public String eliminar(@PathVariable String id) {
        try {
            vehiculoService.eliminar(id);
        } catch (ApiClientException ex) {
            System.err.println("Error al eliminar vehículo: " + ex.getMessage());
        }
        return REDIRECT_LISTA;
    }

    private String construirRedirect(String caracteristicaId) {
        return StringUtils.hasText(caracteristicaId)
                ? REDIRECT_LISTA + "?caracteristicaId=" + caracteristicaId
                : REDIRECT_LISTA;
    }

    private void prepararFormulario(Model model, VehiculoDto dto, String title, boolean modoVer) {
        model.addAttribute("item", dto);
        model.addAttribute("titleForm", title);
        model.addAttribute("modoVer", modoVer);
        cargarCombos(model, dto);
    }

    private void cargarCombos(Model model, VehiculoDto dto) {
        model.addAttribute("estados", EstadoVehiculo.values());
        try {
            List<CaracteristicaVehiculoDto> caracteristicas = caracteristicaService.listar();
            CaracteristicaVehiculoDto seleccionada = null;
            if (dto != null && StringUtils.hasText(dto.getCaracteristicaVehiculoId())) {
                String idSeleccionado = dto.getCaracteristicaVehiculoId();
                seleccionada = caracteristicas.stream()
                        .filter(c -> idSeleccionado.equals(c.getId()))
                        .findFirst()
                        .orElseGet(() -> caracteristicaService.consultar(idSeleccionado));
                if (seleccionada != null) {
                    caracteristicas = Collections.singletonList(seleccionada);
                }
            }
            model.addAttribute("caracteristicas", caracteristicas);
            model.addAttribute("caracteristicaSeleccionada", seleccionada);
        } catch (ApiClientException ex) {
            model.addAttribute("caracteristicas", Collections.emptyList());
            model.addAttribute("caracteristicaSeleccionada", null);
            if (!model.containsAttribute("errorMessage")) {
                model.addAttribute("errorMessage", ex.getMessage());
            }
        }
    }

    private Map<String, String> obtenerMapaCaracteristicas() {
        try {
            return caracteristicaService.listar().stream()
                    .collect(Collectors.toMap(CaracteristicaVehiculoDto::getId, this::descripcionCaracteristica));
        } catch (ApiClientException ex) {
            return Collections.emptyMap();
        }
    }

    private String descripcionCaracteristica(CaracteristicaVehiculoDto dto) {
        if (dto == null) {
            return "";
        }
        String marca = dto.getMarca() != null ? dto.getMarca() : "";
        String modelo = dto.getModelo() != null ? dto.getModelo() : "";
        String anio = dto.getAnio() != null ? dto.getAnio().toString() : "";
        return String.format("%s %s %s", marca, modelo, anio).trim();
    }
}
