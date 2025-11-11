package com.car.clientead.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.car.clientead.business.logic.CaracteristicaVehiculoService;
import com.car.clientead.business.logic.CostoVehiculoService;
import com.car.clientead.client.dto.CaracteristicaVehiculoDto;
import com.car.clientead.client.dto.CostoVehiculoDto;
import com.car.clientead.client.exception.ApiClientException;

@Controller
@RequestMapping("/costos-vehiculo")
public class CostoVehiculoController {

    private static final String REDIRECT_LISTA = "redirect:/costos-vehiculo";

    @Autowired
    private CostoVehiculoService service;
    @Autowired
    private CaracteristicaVehiculoService caracteristicaService;

    @GetMapping
    public String listar(@RequestParam(value = "caracteristicaId", required = false) String caracteristicaId,
                         Model model) {
        try {
            List<CostoVehiculoDto> costos = StringUtils.hasText(caracteristicaId)
                    ? service.listarPorCaracteristica(caracteristicaId)
                    : service.listar();
            CaracteristicaVehiculoDto caracteristicaSeleccionada = null;
            if (StringUtils.hasText(caracteristicaId)) {
                caracteristicaSeleccionada = caracteristicaService.consultar(caracteristicaId);
            }
            model.addAttribute("items", costos);
            model.addAttribute("caracteristicaSeleccionada", caracteristicaSeleccionada);
            model.addAttribute("caracteristicaIdFiltro", caracteristicaId);
        } catch (ApiClientException ex) {
            model.addAttribute("items", Collections.emptyList());
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("caracteristicaSeleccionada", null);
            model.addAttribute("caracteristicaIdFiltro", caracteristicaId);
        }
        model.addAttribute("titleList", "Listado de Costos de Vehículo");
        return "lCostoVehiculo.html";
    }

    @GetMapping("/alta")
    public String mostrarFormularioAlta(@RequestParam(value = "caracteristicaId", required = false) String caracteristicaId,
                                        Model model) {
        CostoVehiculoDto dto = new CostoVehiculoDto();
        if (StringUtils.hasText(caracteristicaId)) {
            try {
                CaracteristicaVehiculoDto car = caracteristicaService.consultar(caracteristicaId);
                dto.setCaracteristicaVehiculoDto(car);
            } catch (ApiClientException ex) {
                model.addAttribute("errorMessage", ex.getMessage());
            }
        }
        prepararFormulario(model, dto, "Alta de Costo de Vehículo", false);
        return "eCostoVehiculo.html";
    }

    @PostMapping("/alta")
    public String crear(@ModelAttribute CostoVehiculoDto dto, Model model) {
        try {
            service.crear(dto);
            return construirRedirect(dto);
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
            return construirRedirect(dto);
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
        if (dto != null && dto.getCaracteristicaVehiculoDto() == null) {
            dto.setCaracteristicaVehiculoDto(new CaracteristicaVehiculoDto());
        }
        model.addAttribute("item", dto);
        model.addAttribute("titleForm", title);
        model.addAttribute("modoVer", modoVer);
        try {
            List<CaracteristicaVehiculoDto> caracteristicas = caracteristicaService.listar();
            CaracteristicaVehiculoDto seleccionada = null;
            if (dto != null && dto.getCaracteristicaVehiculoDto() != null &&
                    StringUtils.hasText(dto.getCaracteristicaVehiculoDto().getId())) {
                String idSeleccionado = dto.getCaracteristicaVehiculoDto().getId();
                seleccionada = caracteristicas.stream()
                        .filter(c -> idSeleccionado.equals(c.getId()))
                        .findFirst()
                        .orElseGet(() -> caracteristicaService.consultar(idSeleccionado));
                if (seleccionada != null) {
                    dto.setCaracteristicaVehiculoDto(seleccionada);
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

    private String construirRedirect(CostoVehiculoDto dto) {
        String caracteristicaId = dto != null && dto.getCaracteristicaVehiculoDto() != null
                ? dto.getCaracteristicaVehiculoDto().getId()
                : null;
        return StringUtils.hasText(caracteristicaId)
                ? REDIRECT_LISTA + "?caracteristicaId=" + caracteristicaId
                : REDIRECT_LISTA;
    }
}
