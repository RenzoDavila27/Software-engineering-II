package com.fioritech.gimnasio.controller.view;

import com.fioritech.gimnasio.business.domain.CuotaMensual;
import com.fioritech.gimnasio.business.domain.DetalleFactura;
import com.fioritech.gimnasio.business.domain.Factura;
import com.fioritech.gimnasio.business.domain.FormaDePago;
import com.fioritech.gimnasio.business.domain.enums.EstadoFactura;
import com.fioritech.gimnasio.business.domain.Usuario;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.logic.service.CuotaMensualService;
import com.fioritech.gimnasio.business.logic.service.FacturaService;
import com.fioritech.gimnasio.business.logic.service.FormaDePagoService;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FacturaController {

    private final FacturaService facturaService;
    private final FormaDePagoService formaDePagoService;
    private final CuotaMensualService cuotaMensualService;

    public FacturaController(FacturaService facturaService,
        FormaDePagoService formaDePagoService, CuotaMensualService cuotaMensualService) {
        this.facturaService = facturaService;
        this.formaDePagoService = formaDePagoService;
        this.cuotaMensualService = cuotaMensualService;
    }

    private void cargarCatalogos(Model model) {
        List<FormaDePago> formas = formaDePagoService.listarFormasDePagoActivas();
        List<CuotaMensual> cuotas = Collections.emptyList();
        try {
            cuotas = cuotaMensualService.listarCuotaMensualActiva();
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
        }
        model.addAttribute("formasPago", formas);
        model.addAttribute("cuotasDisponibles", cuotas);
        model.addAttribute("estadosFactura", EstadoFactura.values());
    }

    private void prepararSeleccionCuotas(Model model, Factura factura) {
        List<String> seleccionadas = factura.getDetalles().stream()
            .map(DetalleFactura::getCuotaMensual)
            .map(CuotaMensual::getId)
            .collect(Collectors.toList());
        model.addAttribute("cuotasSeleccionadas", seleccionadas);
        model.addAttribute("socioResumen", obtenerNombreSocioDesdeDetalles(factura));
    }

    @GetMapping("/factura/listaFactura")
    public String listaFactura(Model model, HttpSession session) {
        String rol = (String) session.getAttribute("rol");
        if ("SOCIO".equals(rol)) {
            Usuario usuario = (Usuario) session.getAttribute("usuarioSession");
            String usuarioId = usuario != null ? usuario.getId() : null;
            List<Factura> lista = facturaService.listarFacturaPorUsuario(usuarioId);
            model.addAttribute("listaFactura", lista);
        } else {
            List<Factura> lista = facturaService.listarFacturaActivo();
            model.addAttribute("listaFactura", lista);
        }
        return "view/factura/lFactura";
    }

    @GetMapping("/factura/altaFactura")
    public String alta(Factura factura, Model model) {
        model.addAttribute("isDisabled", false);
        model.addAttribute("cuotasSeleccionadas", Collections.emptyList());
        model.addAttribute("socioResumen", null);
        cargarCatalogos(model);
        return "view/factura/eFactura";
    }

    @GetMapping("/factura/consultar/{id}")
    public String consultar(@PathVariable("id") String idFactura, Model model, RedirectAttributes attributes) {
        try {
            Factura factura = facturaService.buscarFactura(idFactura);
            model.addAttribute("factura", factura);
            model.addAttribute("isDisabled", true);
            prepararSeleccionCuotas(model, factura);
            cargarCatalogos(model);
            return "view/factura/eFactura";
        } catch (BusinessException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
            return "redirect:/factura/listaFactura";
        }
    }

    @GetMapping("/factura/modificar/{id}")
    public String modificar(@PathVariable("id") String idFactura, Model model, RedirectAttributes attributes) {
        try {
            Factura factura = facturaService.buscarFactura(idFactura);
            model.addAttribute("factura", factura);
            model.addAttribute("isDisabled", false);
            prepararSeleccionCuotas(model, factura);
            cargarCatalogos(model);
            return "view/factura/eFactura";
        } catch (BusinessException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
            return "redirect:/factura/listaFactura";
        }
    }

    @GetMapping("/factura/baja/{id}")
    public String baja(@PathVariable("id") String idFactura, RedirectAttributes attributes) {
        try {
            facturaService.eliminarFactura(idFactura);
            attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
        } catch (BusinessException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
        }
        return "redirect:/factura/listaFactura";
    }

    @PostMapping("/factura/aceptarEditFactura")
    public String aceptarEdit(Factura factura, BindingResult result,
        @RequestParam(value = "cuotasSeleccionadas", required = false) List<String> cuotasSeleccionadas,
        RedirectAttributes attributes, Model model) {

        List<String> cuotas = cuotasSeleccionadas == null ? Collections.emptyList() : cuotasSeleccionadas;

        try {
            if (result.hasErrors()) {
                model.addAttribute("msgError", "Error de Sistema");
                cargarCatalogos(model);
                model.addAttribute("cuotasSeleccionadas", cuotas);
                model.addAttribute("socioResumen", obtenerNombreSocioDesdeCuotas(cuotas));
                return "view/factura/eFactura";
            }

            String socioId = obtenerSocioDesdeCuotas(cuotas);
            String socioNombre = obtenerNombreSocioDesdeCuotas(cuotas);

            if (factura.getId() == null || factura.getId().trim().isEmpty()) {
                BigDecimal total = factura.getTotalPagado();
                facturaService.crearFactura(
                    factura.getNumeroFactura(),
                    factura.getFechaFactura(),
                    total != null ? total.doubleValue() : 0d,
                    factura.getEstado(),
                    socioId,
                    factura.getFormaDePago() != null ? factura.getFormaDePago().getId() : null,
                    cuotas
                );
            } else {
                facturaService.modificarFactura(
                    factura.getId(),
                    factura.getNumeroFactura(),
                    factura.getFechaFactura(),
                    factura.getTotalPagado() != null ? factura.getTotalPagado().doubleValue() : null,
                    factura.getEstado(),
                    cuotas.isEmpty() ? null : cuotas
                );
            }

            attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
            return "redirect:/factura/listaFactura";

        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessage());
            cargarCatalogos(model);
            model.addAttribute("cuotasSeleccionadas", cuotas);
            model.addAttribute("socioResumen", obtenerNombreSocioDesdeCuotas(cuotas));
            return "view/factura/eFactura";
        } catch (Exception e) {
            model.addAttribute("msgError", "Error de Sistema");
            cargarCatalogos(model);
            model.addAttribute("cuotasSeleccionadas", cuotas);
            model.addAttribute("socioResumen", obtenerNombreSocioDesdeCuotas(cuotas));
            return "view/factura/eFactura";
        }
    }

    @GetMapping("/factura/cancelarEditFactura")
    public String cancelarEdit() {
        return "redirect:/factura/listaFactura";
    }

    private String obtenerSocioDesdeCuotas(List<String> cuotasSeleccionadas) {
        if (cuotasSeleccionadas == null || cuotasSeleccionadas.isEmpty()) {
            throw new BusinessException("Debe seleccionar al menos una cuota mensual");
        }
        CuotaMensual primera = cuotaMensualService.buscarCuotaMensual(cuotasSeleccionadas.get(0));
        String socioId = primera.getSocio().getId();
        for (String idCuota : cuotasSeleccionadas) {
            CuotaMensual cuota = cuotaMensualService.buscarCuotaMensual(idCuota);
            if (!cuota.getSocio().getId().equals(socioId)) {
                throw new BusinessException("Todas las cuotas seleccionadas deben pertenecer al mismo socio");
            }
        }
        return socioId;
    }

    private String obtenerNombreSocioDesdeCuotas(List<String> cuotasSeleccionadas) {
        if (cuotasSeleccionadas == null || cuotasSeleccionadas.isEmpty()) {
            return null;
        }
        try {
            CuotaMensual cuota = cuotaMensualService.buscarCuotaMensual(cuotasSeleccionadas.get(0));
            return cuota.getSocio().getNombre() + " " + cuota.getSocio().getApellido();
        } catch (BusinessException e) {
            return null;
        }
    }

    private String obtenerNombreSocioDesdeDetalles(Factura factura) {
        if (factura == null || factura.getDetalles() == null || factura.getDetalles().isEmpty()) {
            return null;
        }
        CuotaMensual cuota = factura.getDetalles().get(0).getCuotaMensual();
        return cuota.getSocio().getNombre() + " " + cuota.getSocio().getApellido();
    }
}
