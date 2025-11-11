package com.car.clientead.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.car.clientead.business.logic.AlquilerService;
import com.car.clientead.business.logic.ClienteService;
import com.car.clientead.business.logic.DocumentacionService;
import com.car.clientead.business.logic.FacturaService;
import com.car.clientead.business.logic.VehiculoService;
import com.car.clientead.business.logic.view.FacturaDetalleView;
import com.car.clientead.client.dto.AlquilerDto;
import com.car.clientead.client.dto.ClienteDto;
import com.car.clientead.client.dto.DocumentacionDto;
import com.car.clientead.client.dto.VehiculoDto;
import com.car.clientead.client.dto.enums.TipoDocumentacion;
import com.car.clientead.client.exception.ApiClientException;

@Controller
@RequestMapping("/alquileres")
public class AlquilerController {

    private static final String LIST_VIEW = "lAlquiler.html";
    private static final String FORM_VIEW = "eAlquiler.html";
    private static final String REDIRECT_LISTA = "redirect:/alquileres";

    @Autowired
    private AlquilerService alquilerService;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private VehiculoService vehiculoService;
    @Autowired
    private DocumentacionService documentacionService;
    @Autowired
    private FacturaService facturaService;

    @GetMapping
    public String listar(Model model) {
        try {
            List<AlquilerDto> alquileres = alquilerService.listar();
            Map<String, ClienteDto> clienteMap = clienteService.listarClientesBasicos().stream()
                    .collect(Collectors.toMap(ClienteDto::getId, c -> c));
            Map<String, VehiculoDto> vehiculoMap = vehiculoService.listar().stream()
                    .collect(Collectors.toMap(VehiculoDto::getId, v -> v));
            Map<String, String> facturasPorAlquiler = facturaService.mapearFacturaPorAlquiler();

            model.addAttribute("items", alquileres);
            model.addAttribute("clienteMap", clienteMap);
            model.addAttribute("vehiculoMap", vehiculoMap);
            model.addAttribute("facturas", facturasPorAlquiler);
        } catch (ApiClientException ex) {
            model.addAttribute("items", Collections.emptyList());
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("clienteMap", Collections.emptyMap());
            model.addAttribute("vehiculoMap", Collections.emptyMap());
            model.addAttribute("facturas", Collections.emptyMap());
        }
        model.addAttribute("titleList", "Gestión de Alquileres");
        return LIST_VIEW;
    }

    @GetMapping("/alta")
    public String alta(@RequestParam(value = "clienteId", required = false) String clienteId,
                       Model model) {
        AlquilerDto dto = new AlquilerDto();
        if (StringUtils.hasText(clienteId)) {
            dto.setClienteId(clienteId);
        }
        prepararFormulario(model, dto, "Registrar alquiler", false);
        return FORM_VIEW;
    }

    @PostMapping("/alta")
    public String crear(@ModelAttribute AlquilerDto dto,
                        @RequestParam("tipoDocumentacion") TipoDocumentacion tipoDocumentacion,
                        @RequestParam(value = "observacionDocumentacion", required = false) String observacion,
                        @RequestParam("documentos") MultipartFile[] archivos,
                        Model model) {
        try {
            alquilerService.crear(dto, tipoDocumentacion, observacion, archivos);
            return REDIRECT_LISTA;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            prepararFormulario(model, dto, "Registrar alquiler", false);
            return FORM_VIEW;
        }
    }

    @GetMapping("/consultar/{id}")
    public String consultar(@PathVariable String id, Model model) {
        try {
            AlquilerDto dto = alquilerService.consultar(id);
            prepararFormulario(model, dto, "Detalle del alquiler", true);
            return FORM_VIEW;
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_LISTA;
        }
    }

    @GetMapping("/modificar/{id}")
    public String editar(@PathVariable String id, Model model) {
        try {
            AlquilerDto dto = alquilerService.consultar(id);
            prepararFormulario(model, dto, "Modificar alquiler", false);
            return FORM_VIEW;
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_LISTA;
        }
    }

    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable String id,
                            @ModelAttribute AlquilerDto dto,
                            @RequestParam("tipoDocumentacion") TipoDocumentacion tipoDocumentacion,
                            @RequestParam(value = "observacionDocumentacion", required = false) String observacion,
                            @RequestParam(value = "documentos", required = false) MultipartFile[] archivos,
                            Model model) {
        try {
            alquilerService.modificar(id, dto, tipoDocumentacion, observacion, archivos);
            return REDIRECT_LISTA;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            prepararFormulario(model, dto, "Modificar alquiler", false);
            return FORM_VIEW;
        }
    }

    @GetMapping("/baja/{id}")
    public String eliminar(@PathVariable String id) {
        try {
            AlquilerDto dto = alquilerService.consultar(id);
            if (dto != null && StringUtils.hasText(dto.getDocumentacionId())) {
                documentacionService.eliminar(dto.getDocumentacionId());
            }
            alquilerService.eliminar(id);
        } catch (ApiClientException ex) {
            System.err.println("Error al eliminar alquiler: " + ex.getMessage());
        }
        return REDIRECT_LISTA;
    }

    @GetMapping("/factura/{alquilerId}")
    public String verFactura(@PathVariable String alquilerId, Model model) {
        try {
            FacturaDetalleView vista = facturaService.buscarPorAlquiler(alquilerId)
                    .orElseThrow(() -> new ApiClientException("El alquiler seleccionado no tiene una factura asociada."));
            model.addAttribute("vista", vista);
            model.addAttribute("title", "Factura del alquiler");
            return "vFactura.html";
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_LISTA;
        }
    }

    @GetMapping("/factura/{alquilerId}/pdf")
    public ResponseEntity<byte[]> descargarFactura(@PathVariable String alquilerId) {
        return facturaService.buscarPorAlquiler(alquilerId)
                .map(vista -> {
                    byte[] pdf = facturaService.generarPdf(vista);
                    String nombre = "factura-" + vista.getFactura().getNumeroFactura() + ".pdf";
                    return ResponseEntity.ok()
                            .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                                    "attachment; filename=\"" + nombre + "\"")
                            .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                            .body(pdf);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private void prepararFormulario(Model model, AlquilerDto dto, String titulo, boolean modoVer) {
        List<ClienteDto> clientes = obtenerClientes();
        List<VehiculoDto> vehiculos = vehiculoService.listar();
        ClienteDto clienteSeleccionado = null;
        if (dto != null && StringUtils.hasText(dto.getClienteId())) {
            String id = dto.getClienteId();
            clienteSeleccionado = clientes.stream()
                    .filter(c -> id.equals(c.getId()))
                    .findFirst()
                    .orElse(null);
        }
        VehiculoDto vehiculoSeleccionado = null;
        if (dto != null && StringUtils.hasText(dto.getVehiculoId())) {
            String id = dto.getVehiculoId();
            vehiculoSeleccionado = vehiculos.stream()
                    .filter(v -> id.equals(v.getId()))
                    .findFirst()
                    .orElse(null);
        }

        model.addAttribute("item", dto);
        model.addAttribute("titleForm", titulo);
        model.addAttribute("modoVer", modoVer);
        model.addAttribute("clientes", clientes);
        model.addAttribute("vehiculos", vehiculos);
        model.addAttribute("clienteSeleccionado", clienteSeleccionado);
        model.addAttribute("vehiculoSeleccionado", vehiculoSeleccionado);
        model.addAttribute("tiposDocumentacion", TipoDocumentacion.values());

        if (dto != null && StringUtils.hasText(dto.getDocumentacionId())) {
            try {
                DocumentacionDto doc = documentacionService.consultar(dto.getDocumentacionId());
                model.addAttribute("documentacion", doc);
            } catch (ApiClientException ex) {
                model.addAttribute("documentacion", null);
                model.addAttribute("errorMessage", ex.getMessage());
            }
        } else {
            model.addAttribute("documentacion", null);
        }
    }

    private List<ClienteDto> obtenerClientes() {
        try {
            return clienteService.listarClientesBasicos();
        } catch (ApiClientException ex) {
            return Collections.emptyList();
        }
    }
}
