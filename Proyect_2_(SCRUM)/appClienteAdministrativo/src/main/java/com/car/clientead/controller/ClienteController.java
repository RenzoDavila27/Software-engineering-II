package com.car.clientead.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.car.clientead.business.logic.ClienteService;
import com.car.clientead.business.logic.NacionalidadService;
import com.car.clientead.business.logic.view.ClienteResumenView;
import com.car.clientead.client.dto.ClienteDto;
import com.car.clientead.client.dto.NacionalidadDto;
import com.car.clientead.client.dto.enums.TipoDocumento;
import com.car.clientead.client.exception.ApiClientException;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private static final String REDIRECT_CLIENTES = "redirect:/clientes";
    private static final String FORM_VIEW = "eCliente.html";
    private static final String LIST_VIEW = "lCliente.html";

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private NacionalidadService nacionalidadService;

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("items", clienteService.listarResumenes());
        } catch (ApiClientException ex) {
            model.addAttribute("items", Collections.emptyList());
            model.addAttribute("errorMessage", ex.getMessage());
        }
        model.addAttribute("titleList", "Listado de Clientes");
        model.addAttribute("filtroDesde", null);
        model.addAttribute("filtroHasta", null);
        return LIST_VIEW;
    }

    @GetMapping("/alta")
    public String mostrarAlta(Model model) {
        prepararFormulario(model, new ClienteDto(), "Alta de Cliente", false);
        return FORM_VIEW;
    }

    @PostMapping("/alta")
    public String crear(@ModelAttribute ClienteDto dto, Model model) {
        try {
            clienteService.crear(dto);
            return REDIRECT_CLIENTES;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            prepararFormulario(model, dto, "Alta de Cliente", false);
            return FORM_VIEW;
        }
    }

    @GetMapping("/consultar/{id}")
    public String consultar(@PathVariable String id, Model model) {
        try {
            ClienteResumenView resumen = clienteService.obtenerResumen(id);
            ClienteDto dto = resumen.getCliente();
            prepararFormulario(model, dto, "Detalle del Cliente", true, resumen);
            return FORM_VIEW;
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_CLIENTES;
        }
    }

    @GetMapping("/modificar/{id}")
    public String editar(@PathVariable String id, Model model) {
        try {
            ClienteDto dto = clienteService.consultar(id);
            prepararFormulario(model, dto, "Modificar Cliente", false);
            return FORM_VIEW;
        } catch (ApiClientException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return REDIRECT_CLIENTES;
        }
    }

    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable String id, @ModelAttribute ClienteDto dto, Model model) {
        try {
            clienteService.modificar(id, dto);
            return REDIRECT_CLIENTES;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            prepararFormulario(model, dto, "Modificar Cliente", false);
            return FORM_VIEW;
        }
    }

    @GetMapping("/baja/{id}")
    public String eliminar(@PathVariable String id) {
        try {
            clienteService.eliminar(id);
        } catch (ApiClientException ex) {
            System.err.println("Error al eliminar cliente: " + ex.getMessage());
        }
        return REDIRECT_CLIENTES;
    }

    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable String id) {
        try {
            byte[] pdf = clienteService.generarPdf(id);
            String fileName = "cliente-" + id + ".pdf";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (ApiClientException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        try {
            byte[] excel = clienteService.generarExcelAlquileres(desde, hasta);
            String fileName = String.format("alquileres-%s-%s.xlsx", desde, hasta);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excel);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    private void prepararFormulario(Model model, ClienteDto dto, String titulo, boolean modoVer) {
        prepararFormulario(model, dto, titulo, modoVer, null);
    }

    private void prepararFormulario(Model model, ClienteDto dto, String titulo, boolean modoVer, ClienteResumenView resumen) {
        model.addAttribute("item", dto);
        model.addAttribute("titleForm", titulo);
        model.addAttribute("modoVer", modoVer);
        model.addAttribute("tiposDocumento", TipoDocumento.values());
        cargarNacionalidades(model);

        if (modoVer) {
            ClienteResumenView view = resumen != null ? resumen : clienteService.obtenerResumen(dto.getId());
            model.addAttribute("detalleContacto", view.getContactoResumen());
            model.addAttribute("detalleDireccion", view.getDireccionResumen());
            model.addAttribute("detalleFechaNacimiento", view.getCliente().getFechaNacimiento());
            model.addAttribute("detalleDireccionEstadia", view.getCliente().getDireccionEstadia());
            model.addAttribute("imagenDataUrl", clienteService.obtenerImagenDataUrl(view.getCliente().getImagenId()));
        } else {
            model.addAttribute("detalleContacto", null);
            model.addAttribute("detalleDireccion", null);
            model.addAttribute("detalleFechaNacimiento", null);
            model.addAttribute("detalleDireccionEstadia", null);
            model.addAttribute("imagenDataUrl", null);
        }
    }

    private void cargarNacionalidades(Model model) {
        try {
            model.addAttribute("nacionalidades", nacionalidadService.listar());
        } catch (ApiClientException ex) {
            model.addAttribute("nacionalidades", Collections.<NacionalidadDto>emptyList());
            Object existing = model.asMap().get("errorMessage");
            String message = existing != null
                    ? existing.toString() + " " + ex.getMessage()
                    : ex.getMessage();
            model.addAttribute("errorMessage", message);
        }
    }
}
