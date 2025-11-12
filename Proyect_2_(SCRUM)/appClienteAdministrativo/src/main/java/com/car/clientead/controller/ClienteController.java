package com.car.clientead.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

import jakarta.servlet.http.HttpServletRequest;

import com.car.clientead.business.logic.ClienteCatalogoService;
import com.car.clientead.business.logic.ClienteService;
import com.car.clientead.business.logic.LocalidadService;
import com.car.clientead.business.logic.NacionalidadService;
import com.car.clientead.business.logic.view.ClienteResumenView;
import com.car.clientead.client.dto.ClienteDto;
import com.car.clientead.client.dto.ContactoCorreoElectronicoDto;
import com.car.clientead.client.dto.ContactoTelefonicoDto;
import com.car.clientead.client.dto.DireccionDto;
import com.car.clientead.client.dto.ImagenDto;
import com.car.clientead.client.dto.LocalidadDto;
import com.car.clientead.client.dto.NacionalidadDto;
import com.car.clientead.client.dto.enums.TipoContacto;
import com.car.clientead.client.dto.enums.TipoDocumento;
import com.car.clientead.client.dto.enums.TipoImagen;
import com.car.clientead.client.dto.enums.TipoTelefono;
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

    @Autowired
    private ClienteCatalogoService clienteCatalogoService;

    @Autowired
    private LocalidadService localidadService;

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
    public String crear(@ModelAttribute ClienteDto dto,
                        @ModelAttribute("nuevoContactoTelefonico") ContactoTelefonicoDto nuevoContactoTelefonico,
                        @ModelAttribute("nuevoContactoCorreo") ContactoCorreoElectronicoDto nuevoContactoCorreo,
                        @ModelAttribute("nuevaDireccion") DireccionDto nuevaDireccion,
                        @ModelAttribute("nuevaImagen") ImagenDto nuevaImagen,
                        @RequestParam(name = "tipoContactoNuevo", required = false) String tipoContactoNuevo,
                        @RequestParam(name = "nuevaImagen.nombre", required = false) String nombreImagenCampo,
                        @RequestParam(name = "imagenArchivo", required = false) MultipartFile imagenArchivo,
                        HttpServletRequest request,
                        Model model) {
        try {
            sincronizarContactosDesdeRequest(request, nuevoContactoTelefonico, nuevoContactoCorreo);
            if (StringUtils.hasText(nombreImagenCampo)) {
                nuevaImagen.setNombre(nombreImagenCampo);
            }
            prepararContenidoImagen(nuevaImagen, imagenArchivo);
            String tipoContacto = StringUtils.hasText(tipoContactoNuevo) ? tipoContactoNuevo : "TELEFONO";
            clienteService.crearConDatosRelacionados(
                    dto,
                    nuevaDireccion,
                    nuevaImagen,
                    nuevoContactoTelefonico,
                    nuevoContactoCorreo,
                    tipoContacto
            );
            return REDIRECT_CLIENTES;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            String tipoSeleccionado = StringUtils.hasText(tipoContactoNuevo) ? tipoContactoNuevo : "TELEFONO";
            model.addAttribute("tipoContactoNuevo", tipoSeleccionado);
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
    public String modificar(@PathVariable String id,
                            @ModelAttribute ClienteDto dto,
                            @ModelAttribute("nuevoContactoTelefonico") ContactoTelefonicoDto nuevoContactoTelefonico,
                            @ModelAttribute("nuevoContactoCorreo") ContactoCorreoElectronicoDto nuevoContactoCorreo,
                            @ModelAttribute("nuevaDireccion") DireccionDto nuevaDireccion,
                            @ModelAttribute("nuevaImagen") ImagenDto nuevaImagen,
                            @RequestParam(name = "tipoContactoNuevo", required = false) String tipoContactoNuevo,
                            @RequestParam(name = "nuevaImagen.nombre", required = false) String nombreImagenCampo,
                            @RequestParam(name = "imagenArchivo", required = false) MultipartFile imagenArchivo,
                            HttpServletRequest request,
                            Model model) {
        try {
            sincronizarContactosDesdeRequest(request, nuevoContactoTelefonico, nuevoContactoCorreo);
            if (StringUtils.hasText(nombreImagenCampo)) {
                nuevaImagen.setNombre(nombreImagenCampo);
            }
            prepararContenidoImagenEdicion(dto, nuevaImagen, imagenArchivo);
            String tipoContacto = StringUtils.hasText(tipoContactoNuevo) ? tipoContactoNuevo : "TELEFONO";
            clienteService.modificarConDatosRelacionados(
                    id,
                    dto,
                    nuevaDireccion,
                    nuevaImagen,
                    nuevoContactoTelefonico,
                    nuevoContactoCorreo,
                    tipoContacto
            );
            return REDIRECT_CLIENTES;
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            String tipoSeleccionado = StringUtils.hasText(tipoContactoNuevo) ? tipoContactoNuevo : "TELEFONO";
            model.addAttribute("tipoContactoNuevo", tipoSeleccionado);
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
        model.addAttribute("tiposImagen", TipoImagen.values());
        model.addAttribute("tiposTelefono", TipoTelefono.values());
        model.addAttribute("tiposContacto", TipoContacto.values());

        boolean esAlta = !StringUtils.hasText(dto.getId());
        boolean mostrarRelacionesInline = !modoVer;
        model.addAttribute("esAltaCliente", esAlta);
        model.addAttribute("mostrarRelacionesInline", mostrarRelacionesInline);

        if (mostrarRelacionesInline) {
            if (esAlta) {
                inicializarRelacionesAlta(model);
            } else {
                inicializarRelacionesEdicion(model, dto);
            }
        }

        cargarNacionalidades(model);
        cargarCatalogosCliente(model);
        cargarLocalidades(model);

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
            appendError(model, ex.getMessage());
        }
    }

    private void inicializarRelacionesAlta(Model model) {
        ensureAttribute(model, "nuevoContactoTelefonico", this::crearContactoTelefonicoPorDefecto);
        ensureAttribute(model, "nuevoContactoCorreo", this::crearContactoCorreoPorDefecto);
        ensureAttribute(model, "nuevaDireccion", DireccionDto::new);
        ensureAttribute(model, "nuevaImagen", () -> {
            ImagenDto imagen = new ImagenDto();
            imagen.setTipoImagen(TipoImagen.PERSONA);
            return imagen;
        });
        if (!model.containsAttribute("tipoContactoNuevo")) {
            model.addAttribute("tipoContactoNuevo", "TELEFONO");
        }
        Object imagenAttr = model.asMap().get("nuevaImagen");
        if (imagenAttr instanceof ImagenDto imagenDto && imagenDto.getTipoImagen() == null) {
            imagenDto.setTipoImagen(TipoImagen.PERSONA);
        }
    }

    private void inicializarRelacionesEdicion(Model model, ClienteDto dto) {
        ContactoTelefonicoDto contactoTel = clienteService.obtenerContactoTelefonico(dto.getContactoId());
        ContactoCorreoElectronicoDto contactoCorreo = contactoTel == null
                ? clienteService.obtenerContactoCorreo(dto.getContactoId())
                : null;
        if (!model.containsAttribute("tipoContactoNuevo")) {
            model.addAttribute("tipoContactoNuevo", contactoCorreo != null ? "CORREO" : "TELEFONO");
        }
        ensureAttribute(model, "nuevoContactoTelefonico", () ->
                contactoTel != null ? contactoTel : crearContactoTelefonicoPorDefecto());
        ensureAttribute(model, "nuevoContactoCorreo", () ->
                contactoCorreo != null ? contactoCorreo : crearContactoCorreoPorDefecto());
        ensureAttribute(model, "nuevaDireccion", () -> {
            DireccionDto direccion = clienteService.obtenerDireccion(dto.getDireccionId());
            return direccion != null ? direccion : new DireccionDto();
        });
        ensureAttribute(model, "nuevaImagen", () -> {
            ImagenDto imagen = clienteService.obtenerImagen(dto.getImagenId());
            if (imagen == null) {
                imagen = new ImagenDto();
                imagen.setTipoImagen(TipoImagen.PERSONA);
            }
            return imagen;
        });
    }

    private ContactoTelefonicoDto crearContactoTelefonicoPorDefecto() {
        ContactoTelefonicoDto contacto = new ContactoTelefonicoDto();
        contacto.setTipoTelefono(TipoTelefono.CELULAR);
        contacto.setTipoContacto(TipoContacto.PERSONAL);
        return contacto;
    }

    private ContactoCorreoElectronicoDto crearContactoCorreoPorDefecto() {
        ContactoCorreoElectronicoDto contacto = new ContactoCorreoElectronicoDto();
        contacto.setTipoContacto(TipoContacto.PERSONAL);
        return contacto;
    }

    private void cargarCatalogosCliente(Model model) {
        try {
            model.addAttribute("contactosDisponibles", clienteCatalogoService.listarContactos());
        } catch (ApiClientException ex) {
            model.addAttribute("contactosDisponibles", Collections.emptyList());
            appendError(model, ex.getMessage());
        }
        try {
            model.addAttribute("direccionesDisponibles", clienteCatalogoService.listarDirecciones());
        } catch (ApiClientException ex) {
            model.addAttribute("direccionesDisponibles", Collections.emptyList());
            appendError(model, ex.getMessage());
        }
        try {
            model.addAttribute("imagenesDisponibles", clienteCatalogoService.listarImagenes());
        } catch (ApiClientException ex) {
            model.addAttribute("imagenesDisponibles", Collections.emptyList());
            appendError(model, ex.getMessage());
        }
    }

    private void cargarLocalidades(Model model) {
        try {
            model.addAttribute("localidades", localidadService.listar());
        } catch (ApiClientException ex) {
            model.addAttribute("localidades", Collections.<LocalidadDto>emptyList());
            appendError(model, ex.getMessage());
        }
    }

    private void sincronizarContactosDesdeRequest(HttpServletRequest request,
                                                  ContactoTelefonicoDto contactoTelefonico,
                                                  ContactoCorreoElectronicoDto contactoCorreo) {
        if (contactoTelefonico != null) {
            String telefono = request.getParameter("nuevoContactoTelefonico.telefono");
            if (telefono != null) {
                contactoTelefonico.setTelefono(telefono);
            }
            String tipoTelefono = request.getParameter("nuevoContactoTelefonico.tipoTelefono");
            if (StringUtils.hasText(tipoTelefono)) {
                try {
                    contactoTelefonico.setTipoTelefono(TipoTelefono.valueOf(tipoTelefono));
                } catch (IllegalArgumentException ignored) {
                    contactoTelefonico.setTipoTelefono(null);
                }
            }
            String tipoContactoTel = request.getParameter("nuevoContactoTelefonico.tipoContacto");
            if (StringUtils.hasText(tipoContactoTel)) {
                try {
                    contactoTelefonico.setTipoContacto(TipoContacto.valueOf(tipoContactoTel));
                } catch (IllegalArgumentException ignored) {
                    contactoTelefonico.setTipoContacto(null);
                }
            }
            String observacion = request.getParameter("nuevoContactoTelefonico.observacion");
            if (observacion != null) {
                contactoTelefonico.setObservacion(observacion);
            }
        }

        if (contactoCorreo != null) {
            String email = request.getParameter("nuevoContactoCorreo.email");
            if (email != null) {
                contactoCorreo.setEmail(email);
            }
            String tipoContactoCorreo = request.getParameter("nuevoContactoCorreo.tipoContacto");
            if (StringUtils.hasText(tipoContactoCorreo)) {
                try {
                    contactoCorreo.setTipoContacto(TipoContacto.valueOf(tipoContactoCorreo));
                } catch (IllegalArgumentException ignored) {
                    contactoCorreo.setTipoContacto(null);
                }
            }
            String observacionCorreo = request.getParameter("nuevoContactoCorreo.observacion");
            if (observacionCorreo != null) {
                contactoCorreo.setObservacion(observacionCorreo);
            }
        }
    }

    private void prepararContenidoImagen(ImagenDto nuevaImagen, MultipartFile imagenArchivo) {
        if (nuevaImagen == null) {
            throw new IllegalArgumentException("Los datos de la imagen no pueden ser nulos.");
        }
        if (imagenArchivo == null || imagenArchivo.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar la imagen del cliente.");
        }
        try {
            nuevaImagen.setContenido(imagenArchivo.getBytes());
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo leer el archivo de imagen.", e);
        }
        if (!StringUtils.hasText(nuevaImagen.getMime())) {
            String mime = StringUtils.hasText(imagenArchivo.getContentType()) ? imagenArchivo.getContentType() : "image/png";
            nuevaImagen.setMime(mime);
        }
        if (!StringUtils.hasText(nuevaImagen.getNombre())) {
            String nombre = StringUtils.hasText(imagenArchivo.getOriginalFilename())
                    ? imagenArchivo.getOriginalFilename()
                    : "imagen-cliente";
            nuevaImagen.setNombre(nombre);
        }
    }

    private void prepararContenidoImagenEdicion(ClienteDto cliente,
                                                ImagenDto nuevaImagen,
                                                MultipartFile imagenArchivo) {
        if (imagenArchivo != null && !imagenArchivo.isEmpty()) {
            prepararContenidoImagen(nuevaImagen, imagenArchivo);
            return;
        }
        if (cliente == null || !StringUtils.hasText(cliente.getImagenId())) {
            throw new IllegalArgumentException("No se encontró la imagen asociada al cliente.");
        }
        ImagenDto actual = clienteService.obtenerImagen(cliente.getImagenId());
        if (actual == null || actual.getContenido() == null || actual.getContenido().length == 0) {
            throw new IllegalArgumentException("No se pudo recuperar la imagen actual del cliente.");
        }
        nuevaImagen.setContenido(actual.getContenido());
        if (!StringUtils.hasText(nuevaImagen.getMime())) {
            nuevaImagen.setMime(actual.getMime());
        }
        if (!StringUtils.hasText(nuevaImagen.getNombre())) {
            nuevaImagen.setNombre(actual.getNombre());
        }
        if (nuevaImagen.getTipoImagen() == null) {
            nuevaImagen.setTipoImagen(actual.getTipoImagen());
        }
    }

    private <T> void ensureAttribute(Model model, String attributeName, Supplier<T> supplier) {
        if (!model.containsAttribute(attributeName)) {
            model.addAttribute(attributeName, supplier.get());
        }
    }

    private void appendError(Model model, String newMessage) {
        Object existing = model.asMap().get("errorMessage");
        String message = existing != null
                ? existing.toString() + " " + newMessage
                : newMessage;
        model.addAttribute("errorMessage", message);
    }
}
