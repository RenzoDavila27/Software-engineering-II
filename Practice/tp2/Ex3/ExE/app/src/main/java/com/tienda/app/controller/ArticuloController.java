package com.tienda.app.controller;

import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tienda.app.business.domain.Articulo;
import com.tienda.app.business.domain.Proveedor;
import com.tienda.app.business.domain.Imagen;
import com.tienda.app.business.logic.error.ErrorServiceException;
import com.tienda.app.business.logic.service.ArticuloService;
import com.tienda.app.business.logic.service.ProveedorService;
import com.tienda.app.business.logic.service.ImagenService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/articulo")
public class ArticuloController extends BaseController<Articulo, Long> {

    private final ProveedorService proveedorService;
    private final ImagenService imagenService;

    public ArticuloController(ArticuloService service, ProveedorService proveedorService, ImagenService imagenService) {
        super(service);
        this.proveedorService = proveedorService;
        this.imagenService = imagenService;
        initController(new Articulo(), "LIST ARTÍCULO", "EDIT ARTÍCULO");
    }

    @Override
    protected void preAlta() throws ErrorServiceException {
        ensureProveedorInstance();
        loadProveedores();
    }

    @Override
    protected void preModificacion() throws ErrorServiceException {
        ensureProveedorInstance();
        loadProveedores();
    }

    @Override
    protected void preActualziacion() throws ErrorServiceException {
        loadProveedores();
    }

    @Override
    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute("item") Articulo articulo, RedirectAttributes attributes, Model model) {
        try {
            if (articulo.getProveedor() != null && articulo.getProveedor().getId() != null) {
                Proveedor proveedor = proveedorService.obtenerEntidad(articulo.getProveedor().getId());
                articulo.setProveedor(proveedor);
            }
        } catch (ErrorServiceException e) {
            model.addAttribute("msgError", e.getMessage());
            model.addAttribute("item", articulo);
            model.addAttribute("isDisabled", false);
            model.addAttribute("titleEdit", titleEdit);
            model.addAttribute("nameEntityLower", nameEntityLower);
            try {
                model.addAttribute("proveedores", proveedorService.listarActivos());
            } catch (ErrorServiceException ex) {
                model.addAttribute("msgError", ex.getMessage());
            }
            return viewEdit;
        }
        return super.actualizar(articulo, attributes, model);
    }

    @GetMapping("/{id}/imagenes")
    public String gestionarImagenes(@PathVariable Long id, Model model, RedirectAttributes attributes) {
        try {
            Articulo articulo = service.obtenerEntidad(id);
            model.addAttribute("articulo", articulo);
            model.addAttribute("imagenes", imagenService.listarActivasPorArticulo(id));
            model.addAttribute("titleEdit", "IMÁGENES DEL ARTÍCULO");
            model.addAttribute("nameEntityLower", nameEntityLower);
            return "view/eArticuloImagen";
        } catch (ErrorServiceException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
            return redirectList;
        }
    }

    @PostMapping("/{id}/imagenes")
    public String subirImagenes(@PathVariable Long id,
                                @RequestParam("archivos") MultipartFile[] archivos,
                                RedirectAttributes attributes) {
        try {
            Articulo articulo = service.obtenerEntidad(id);
            boolean agregoAlMenosUna = false;
            StringBuilder errores = new StringBuilder();

            MultipartFile[] archivosProcesar = archivos != null ? archivos : new MultipartFile[0];
            for (MultipartFile archivo : archivosProcesar) {
                if (archivo == null || archivo.isEmpty()) {
                    continue;
                }
                try {
                    Imagen imagen = new Imagen();
                    imagen.setNombre(generarNombreArchivo(archivo));
                    String contentType = archivo.getContentType();
                    imagen.setMime(contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE);
                    imagen.setContenido(archivo.getBytes());
                    imagen.setArticulo(articulo);
                    imagenService.alta(imagen);
                    agregoAlMenosUna = true;
                } catch (IOException | ErrorServiceException ex) {
                    if (errores.length() > 0) {
                        errores.append(" ");
                    }
                    errores.append(ex.getMessage() != null ? ex.getMessage() : "No se pudo cargar una imagen.");
                }
            }

            if (errores.length() > 0) {
                attributes.addFlashAttribute("msgError", errores.toString());
            }

            if (agregoAlMenosUna) {
                attributes.addFlashAttribute("msgExito", "Las imágenes se cargaron correctamente.");
            } else if (errores.length() == 0) {
                attributes.addFlashAttribute("msgError", "Debe seleccionar al menos un archivo válido.");
            }

        } catch (ErrorServiceException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
        }
        return "redirect:/articulo/" + id + "/imagenes";
    }

    @PostMapping("/{id}/imagenes/{imagenId}/eliminar")
    public String eliminarImagen(@PathVariable Long id,
                                 @PathVariable Long imagenId,
                                 RedirectAttributes attributes) {
        try {
            imagenService.baja(imagenId);
            attributes.addFlashAttribute("msgExito", "La imagen fue eliminada correctamente.");
        } catch (ErrorServiceException e) {
            attributes.addFlashAttribute("msgError", e.getMessage());
        }
        return "redirect:/articulo/" + id + "/imagenes";
    }

    @GetMapping("/imagenes/{imagenId}/contenido")
    public ResponseEntity<byte[]> verImagen(@PathVariable Long imagenId) throws ErrorServiceException {
        Imagen imagen = imagenService.obtenerEntidad(imagenId);
        MediaType mediaType = resolverMediaType(imagen.getMime());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imagen.getNombre() + "\"")
                .contentType(mediaType)
                .body(imagen.getContenido());
    }

    private String generarNombreArchivo(MultipartFile archivo) {
        String nombreOriginal = archivo.getOriginalFilename();
        String nombreSanitizado = nombreOriginal != null ? Paths.get(nombreOriginal).getFileName().toString() : "imagen";
        return nombreSanitizado + "-" + System.currentTimeMillis();
    }

    private MediaType resolverMediaType(String mime) {
        try {
            return mime != null ? MediaType.parseMediaType(mime) : MediaType.APPLICATION_OCTET_STREAM;
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    private void ensureProveedorInstance() {
        if (entity != null && entity.getProveedor() == null) {
            entity.setProveedor(new Proveedor());
        }
    }

    private void loadProveedores() throws ErrorServiceException {
        if (model != null) {
            model.addAttribute("proveedores", proveedorService.listarActivos());
        }
    }
}
