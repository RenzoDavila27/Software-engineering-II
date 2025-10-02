package com.fioritech.demo.bussines.controller;

import com.fioritech.demo.bussines.domain.Direccion;
import com.fioritech.demo.bussines.domain.Proveedor;
import com.fioritech.demo.bussines.logic.service.DireccionService;
import com.fioritech.demo.bussines.logic.service.ProveedorService;
import org.springframework.http.CacheControl;
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

import java.util.Collection;

@Controller
@RequestMapping("/proveedor")
public class ProveedorController {

    private final ProveedorService proveedorService;
    private final DireccionService direccionService;

    public ProveedorController(ProveedorService proveedorService, DireccionService direccionService) {
        this.proveedorService = proveedorService;
        this.direccionService = direccionService;
    }

    @GetMapping("/listar")
    public String listarProveedores(Model model) {
        model.addAttribute("proveedores", proveedorService.listarProveedores());
        return "proveedor/listar";
    }

    @GetMapping("/crear")
    public String crearProveedorForm(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        return "proveedor/crear";
    }

    @PostMapping("/crear")
    public String crearProveedor(@ModelAttribute Proveedor proveedor) {
        proveedorService.crearProveedor(proveedor);
        return "redirect:/proveedor/listar";
    }

    @GetMapping("/modificar/{id}")
    public String modificarProveedorForm(@PathVariable Long id, Model model) {
        model.addAttribute("proveedor", proveedorService.buscarProveedorPorId(id));
        return "proveedor/modificar";
    }

    @PostMapping("/modificar/{id}")
    public String modificarProveedor(@PathVariable Long id, @ModelAttribute Proveedor cambios) {
        proveedorService.modificarProveedor(id, cambios);
        return "redirect:/proveedor/listar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProveedor(@PathVariable Long id) {
        proveedorService.eliminarProveedor(id);
        return "redirect:/proveedor/listar";
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportarProveedores() {
        byte[] contenido = proveedorService.exportarProveedoresPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "proveedores.pdf");
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(contenido.length)
                .body(contenido);
    }

    @ModelAttribute("direcciones")
    public Collection<Direccion> cargarDirecciones() {
        return direccionService.listarDirecciones();
    }
}
