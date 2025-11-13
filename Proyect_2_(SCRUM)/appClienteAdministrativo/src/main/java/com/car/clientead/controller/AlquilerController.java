package com.car.clientead.controller;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.car.clientead.business.logic.AlquilerService;
import com.car.clientead.business.logic.CaracteristicaVehiculoService;
import com.car.clientead.business.logic.ClienteService;
import com.car.clientead.business.logic.CostoVehiculoService;
import com.car.clientead.business.logic.DocumentacionService;
import com.car.clientead.business.logic.FacturaService;
import com.car.clientead.business.logic.VehiculoService;
import com.car.clientead.business.logic.view.FacturaDetalleView;
import com.car.clientead.client.dto.AlquilerDto;
import com.car.clientead.client.dto.CaracteristicaVehiculoDto;
import com.car.clientead.client.dto.ClienteDto;
import com.car.clientead.client.dto.CostoVehiculoDto;
import com.car.clientead.client.dto.DocumentacionDto;
import com.car.clientead.client.dto.VehiculoDto;
import com.car.clientead.client.dto.enums.EstadoVehiculo;
import com.car.clientead.client.dto.enums.RolUsuario;
import com.car.clientead.client.dto.enums.TipoDocumentacion;
import com.car.clientead.client.exception.ApiClientException;
import com.car.clientead.web.session.UserSession;

@Controller
@RequestMapping("/alquileres")
public class AlquilerController {

    private static final String LIST_VIEW = "lAlquiler.html";
    private static final String CLIENT_LIST_VIEW = "lAlquilerCliente.html";
    private static final String FORM_VIEW = "eAlquiler.html";
    private static final String REDIRECT_LISTA = "redirect:/alquileres";

    @Autowired
    private AlquilerService alquilerService;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private CaracteristicaVehiculoService caracteristicaVehiculoService;
    @Autowired
    private VehiculoService vehiculoService;
    @Autowired
    private CostoVehiculoService costoVehiculoService;
    @Autowired
    private DocumentacionService documentacionService;
    @Autowired
    private FacturaService facturaService;
    @Autowired
    private UserSession userSession;

    @GetMapping
    public String listar(Model model) {
        if (esRolCliente()) {
            return "redirect:/alquileres/historial";
        }
        if (!puedeGestionarAlquileres()) {
            return "redirect:/";
        }
        try {
            List<AlquilerDto> alquileres = alquilerService.listar();
            Map<String, ClienteDto> clienteMap = clienteService.listarClientesBasicos().stream()
                    .collect(Collectors.toMap(ClienteDto::getId, c -> c));
            Map<String, VehiculoDto> vehiculoMap = vehiculoService.listar().stream()
                    .collect(Collectors.toMap(VehiculoDto::getId, v -> v));
            Map<String, String> facturasPorAlquiler = facturaService.mapearFacturaPorAlquiler();
            marcarEntregadosSegunVehiculo(alquileres, vehiculoMap);

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

    @GetMapping("/historial")
    public String listarHistorialCliente(@RequestParam(value = "clienteId", required = false) String clienteIdParam,
                                         Model model) {
        if (!esRolCliente()) {
            return REDIRECT_LISTA;
        }
        String clienteId = userSession.getClienteId().orElse(clienteIdParam);
        if (!StringUtils.hasText(clienteId)) {
            model.addAttribute("items", Collections.emptyList());
            model.addAttribute("vehiculoMap", Collections.emptyMap());
            model.addAttribute("facturas", Collections.emptyMap());
            model.addAttribute("clienteActual", null);
            model.addAttribute("errorMessage", "No se pudo determinar el cliente asociado a la sesión.");
            model.addAttribute("titleList", "Historial de alquileres");
            return CLIENT_LIST_VIEW;
        }
        try {
            List<AlquilerDto> alquileres = alquilerService.listarPorCliente(clienteId);
            Map<String, VehiculoDto> vehiculoMap = vehiculoService.listar().stream()
                    .collect(Collectors.toMap(VehiculoDto::getId, v -> v));
            Map<String, String> facturasPorAlquiler = facturaService.mapearFacturaPorAlquiler();
            ClienteDto cliente = clienteService.consultar(clienteId);
            marcarEntregadosSegunVehiculo(alquileres, vehiculoMap);

            model.addAttribute("items", alquileres);
            model.addAttribute("vehiculoMap", vehiculoMap);
            model.addAttribute("facturas", facturasPorAlquiler);
            model.addAttribute("clienteActual", cliente);
        } catch (ApiClientException ex) {
            model.addAttribute("items", Collections.emptyList());
            model.addAttribute("vehiculoMap", Collections.emptyMap());
            model.addAttribute("facturas", Collections.emptyMap());
            model.addAttribute("clienteActual", null);
            model.addAttribute("errorMessage", ex.getMessage());
        }
        model.addAttribute("titleList", "Historial de alquileres");
        return CLIENT_LIST_VIEW;
    }

    @GetMapping("/alta")
    public String alta(@RequestParam(value = "clienteId", required = false) String clienteId,
                       @RequestParam(value = "caracteristicaId", required = false) String caracteristicaId,
                       Model model) {
        if (!puedeGestionarAlquileres()) {
            return redireccionSegunRol();
        }
        AlquilerDto dto = new AlquilerDto();
        if (StringUtils.hasText(clienteId)) {
            dto.setClienteId(clienteId);
        }
        if (StringUtils.hasText(caracteristicaId)) {
            dto.setCaracteristicaVehiculoId(caracteristicaId);
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
        if (!puedeGestionarAlquileres()) {
            return redireccionSegunRol();
        }
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
        if (!puedeGestionarAlquileres()) {
            return redireccionSegunRol();
        }
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
        if (!puedeGestionarAlquileres()) {
            return redireccionSegunRol();
        }
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
        if (!puedeGestionarAlquileres()) {
            return redireccionSegunRol();
        }
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
        if (!puedeGestionarAlquileres()) {
            return redireccionSegunRol();
        }
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

    @PostMapping("/{id}/entrega")
    @ResponseBody
    public ResponseEntity<?> registrarEntrega(@PathVariable String id) {
        if (!puedeGestionarAlquileres()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permisos para esta acción.");
        }
        try {
            alquilerService.marcarEntrega(id);
            return ResponseEntity.ok().build();
        } catch (ApiClientException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/{id}/entrega-error")
    @ResponseBody
    public ResponseEntity<?> registrarEntregaConError(@PathVariable String id) {
        if (!puedeGestionarAlquileres()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permisos para esta acción.");
        }
        try {
            alquilerService.marcarEntregaError(id);
            return ResponseEntity.ok().build();
        } catch (ApiClientException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/factura/{alquilerId}")
    public String verFactura(@PathVariable String alquilerId, Model model) {
        try {
            FacturaDetalleView vista = facturaService.buscarPorAlquiler(alquilerId)
                    .orElseThrow(() -> new ApiClientException("El alquiler seleccionado no tiene una factura asociada."));
            if (!puedeVisualizarFactura(vista)) {
                throw new ApiClientException("No tiene permisos para acceder a la factura solicitada.");
            }
            model.addAttribute("vista", vista);
            model.addAttribute("title", "Factura del alquiler");
            return "vFactura.html";
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return esRolCliente() ? "redirect:/alquileres/historial" : REDIRECT_LISTA;
        }
    }

    @GetMapping("/factura/{alquilerId}/pdf")
    public ResponseEntity<byte[]> descargarFactura(@PathVariable String alquilerId) {
        return facturaService.buscarPorAlquiler(alquilerId)
                .map(vista -> {
                    if (!puedeVisualizarFactura(vista)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body((byte[]) null);
                    }
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

    private boolean puedeGestionarAlquileres() {
        RolUsuario rol = userSession.getRolActual();
        return rol == RolUsuario.ADMINISTRATIVO || rol == RolUsuario.JEFE;
    }

    private boolean esRolCliente() {
        return userSession.getRolActual() == RolUsuario.CLIENTE;
    }

    private void marcarEntregadosSegunVehiculo(List<AlquilerDto> alquileres, Map<String, VehiculoDto> vehiculoMap) {
        if (alquileres == null || vehiculoMap == null) {
            return;
        }
        alquileres.forEach(alquiler -> {
            VehiculoDto vehiculo = vehiculoMap.get(alquiler.getVehiculoId());
            boolean entregado = vehiculo == null
                    || (vehiculo.getEstadoVehiculo() != null && vehiculo.getEstadoVehiculo() != EstadoVehiculo.ALQUILADO);
            alquiler.setEntregado(entregado);
        });
    }

    private boolean puedeVisualizarFactura(FacturaDetalleView vista) {
        if (vista == null) {
            return false;
        }
        if (!esRolCliente()) {
            return true;
        }
        return vista.getCliente() != null
                && StringUtils.hasText(vista.getCliente().getId())
                && vista.getCliente().getId().equals(userSession.getClienteId().orElse(null));
    }

    private String redireccionSegunRol() {
        return esRolCliente() ? "redirect:/alquileres/historial" : "redirect:/";
    }

    private List<CaracteristicaVehiculoDto> listarCaracteristicas() {
        try {
            return caracteristicaVehiculoService.listar();
        } catch (ApiClientException ex) {
            return Collections.emptyList();
        }
    }

    private List<VehiculoDto> vehiculosDisponiblesParaSelector(List<VehiculoDto> vehiculos, AlquilerDto dto) {
        String vehiculoActual = dto != null ? dto.getVehiculoId() : null;
        return vehiculos.stream()
                .filter(v -> v != null && (!Boolean.TRUE.equals(v.getEliminado())))
                .filter(v -> v.getEstadoVehiculo() == EstadoVehiculo.DISPONIBLE
                        || (StringUtils.hasText(vehiculoActual) && vehiculoActual.equals(v.getId())))
                .collect(Collectors.toList());
    }

    private ClienteDto buscarCliente(List<ClienteDto> clientes, String id) {
        if (!StringUtils.hasText(id)) {
            return null;
        }
        return clientes.stream()
                .filter(c -> id.equals(c.getId()))
                .findFirst()
                .orElse(null);
    }

    private VehiculoDto buscarVehiculo(List<VehiculoDto> vehiculos, String id) {
        if (!StringUtils.hasText(id)) {
            return null;
        }
        return vehiculos.stream()
                .filter(v -> id.equals(v.getId()))
                .findFirst()
                .orElse(null);
    }

    private Map<String, Double> obtenerCostosVigentes() {
        try {
            List<CostoVehiculoDto> costos = costoVehiculoService.listar();
            Map<String, CostoVehiculoDto> ultimoCosto = new HashMap<>();
            for (CostoVehiculoDto costo : costos) {
                if (costo.getCaracteristicaVehiculoDto() == null
                        || !StringUtils.hasText(costo.getCaracteristicaVehiculoDto().getId())) {
                    continue;
                }
                String caracteristicaId = costo.getCaracteristicaVehiculoDto().getId();
                CostoVehiculoDto actual = ultimoCosto.get(caracteristicaId);
                if (actual == null || esMasReciente(costo, actual)) {
                    ultimoCosto.put(caracteristicaId, costo);
                }
            }
            return ultimoCosto.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getCosto()));
        } catch (ApiClientException ex) {
            return Collections.emptyMap();
        }
    }

    private boolean esMasReciente(CostoVehiculoDto candidato, CostoVehiculoDto actual) {
        LocalDate hastaNuevo = normalizarFecha(candidato.getFechaHasta());
        LocalDate hastaActual = normalizarFecha(actual.getFechaHasta());
        if (hastaNuevo.isAfter(hastaActual)) {
            return true;
        }
        if (hastaNuevo.isBefore(hastaActual)) {
            return false;
        }
        LocalDate desdeNuevo = candidato.getFechaDesde();
        LocalDate desdeActual = actual.getFechaDesde();
        if (desdeNuevo == null) {
            return false;
        }
        if (desdeActual == null) {
            return true;
        }
        return desdeNuevo.isAfter(desdeActual);
    }

    private LocalDate normalizarFecha(LocalDate fecha) {
        return fecha != null ? fecha : LocalDate.of(9999, 1, 1);
    }

    private void prepararFormulario(Model model, AlquilerDto dto, String titulo, boolean modoVer) {
        List<ClienteDto> clientes = obtenerClientes();
        List<VehiculoDto> vehiculos = vehiculoService.listar();
        List<CaracteristicaVehiculoDto> caracteristicas = listarCaracteristicas();
        List<VehiculoDto> vehiculosSelector = vehiculosDisponiblesParaSelector(vehiculos, dto);
        Map<String, Double> costosPorCaracteristica = obtenerCostosVigentes();

        ClienteDto clienteSeleccionado = buscarCliente(clientes, dto != null ? dto.getClienteId() : null);
        VehiculoDto vehiculoSeleccionado = buscarVehiculo(vehiculos, dto != null ? dto.getVehiculoId() : null);
        String caracteristicaSeleccionadaId = dto != null ? dto.getCaracteristicaVehiculoId() : null;
        if (!StringUtils.hasText(caracteristicaSeleccionadaId) && vehiculoSeleccionado != null) {
            caracteristicaSeleccionadaId = vehiculoSeleccionado.getCaracteristicaVehiculoId();
            dto.setCaracteristicaVehiculoId(caracteristicaSeleccionadaId);
        }

        model.addAttribute("item", dto);
        model.addAttribute("titleForm", titulo);
        model.addAttribute("modoVer", modoVer);
        model.addAttribute("clientes", clientes);
        model.addAttribute("caracteristicas", caracteristicas);
        model.addAttribute("vehiculosSelector", vehiculosSelector);
        model.addAttribute("caracteristicaSeleccionadaId", caracteristicaSeleccionadaId);
        model.addAttribute("clienteSeleccionado", clienteSeleccionado);
        model.addAttribute("vehiculoSeleccionado", vehiculoSeleccionado);
        model.addAttribute("tiposDocumentacion", TipoDocumentacion.values());
        model.addAttribute("costosPorCaracteristica", costosPorCaracteristica);

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
